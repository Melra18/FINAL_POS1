
package com.pos.service;

import com.pos.config.ConnectDb;
import com.pos.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ActionHistoryService {

    private ActionHistoryService() {
    }

    private static final Map<String, ActionHandler> HANDLERS =
            new ConcurrentHashMap<>();

    public interface ActionHandler {
        void undo(Connection connection, HistoryAction action)
                throws SQLException;

        void redo(Connection connection, HistoryAction action)
                throws SQLException;
    }

    public static final class HistoryAction {

        private final long actionId;
        private final String moduleName;
        private final String actionType;
        private final String entityType;
        private final Long entityId;
        private final String description;
        private final String beforeState;
        private final String afterState;

        public HistoryAction(
                long actionId,
                String moduleName,
                String actionType,
                String entityType,
                Long entityId,
                String description,
                String beforeState,
                String afterState) {

            this.actionId = actionId;
            this.moduleName = moduleName;
            this.actionType = actionType;
            this.entityType = entityType;
            this.entityId = entityId;
            this.description = description;
            this.beforeState = beforeState;
            this.afterState = afterState;
        }

        public long getActionId() {
            return actionId;
        }

        public String getModuleName() {
            return moduleName;
        }

        public String getActionType() {
            return actionType;
        }

        public String getEntityType() {
            return entityType;
        }

        public Long getEntityId() {
            return entityId;
        }

        public String getDescription() {
            return description;
        }

        public String getBeforeState() {
            return beforeState;
        }

        public String getAfterState() {
            return afterState;
        }
    }

    public static void registerHandler(
            String moduleName,
            String actionType,
            ActionHandler handler) {

        if (moduleName == null || moduleName.trim().isEmpty()
                || actionType == null || actionType.trim().isEmpty()
                || handler == null) {
            throw new IllegalArgumentException(
                    "Module, action type, and handler are required.");
        }

        HANDLERS.put(moduleName + ":" + actionType, handler);
    }

    /*
     * Use this overload when the business operation already has
     * an active database transaction.
     *
     * The caller is responsible for committing or rolling back.
     */
    public static long recordAction(
            Connection conn,
            User user,
            String moduleName,
            String actionType,
            String entityType,
            Long entityId,
            String description,
            String beforeState,
            String afterState) throws SQLException {

        validateUser(user);
        validateConnection(conn);

        // A new action after Undo invalidates the old Redo branch.
        discardRedoBranch(conn, user.getUserId());

        return insertAction(
                conn, user, moduleName, actionType, entityType,
                entityId, description, beforeState, afterState);
    }

    /*
     * Standalone history recording.
     * Uses its own transaction.
     *
     * For business operations, prefer the Connection overload above
     * so the business change and history entry stay atomic.
     */
    public static long recordAction(
            User user,
            String moduleName,
            String actionType,
            String entityType,
            Long entityId,
            String description,
            String beforeState,
            String afterState) throws SQLException {

        validateUser(user);

        try (Connection conn = ConnectDb.getConnection()) {
            validateConnection(conn);
            conn.setAutoCommit(false);

            try {
                long actionId = recordAction(
                        conn, user, moduleName, actionType, entityType,
                        entityId, description, beforeState, afterState);

                conn.commit();
                return actionId;

            } catch (SQLException | RuntimeException ex) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackError) {
                    ex.addSuppressed(rollbackError);
                }
                throw ex;
            } finally {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ignored) {
                    // Connection is closing.
                }
            }
        }
    }

    private static long insertAction(
            Connection conn,
            User user,
            String moduleName,
            String actionType,
            String entityType,
            Long entityId,
            String description,
            String beforeState,
            String afterState) throws SQLException {

        String sql =
                "INSERT INTO action_history "
                + "(action_group_id, module_name, action_type, "
                + "entity_type, entity_id, description, "
                + "before_state, after_state, action_status, "
                + "action_by, action_by_username) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'APPLIED', ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, UUID.randomUUID().toString());
            ps.setString(2, moduleName);
            ps.setString(3, actionType);
            ps.setString(4, entityType);

            if (entityId == null) {
                ps.setNull(5, Types.BIGINT);
            } else {
                ps.setLong(5, entityId);
            }

            ps.setString(6, description);
            ps.setString(7, beforeState);
            ps.setString(8, afterState);
            ps.setInt(9, user.getUserId());
            ps.setString(10, user.getUsername());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }

        throw new SQLException(
                "Could not retrieve the history action ID.");
    }

    private static void discardRedoBranch(
            Connection conn, int userId) throws SQLException {

        String sql =
                "UPDATE action_history "
                + "SET action_status = 'DISCARDED' "
                + "WHERE action_by = ? "
                + "AND action_status = 'UNDONE'";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

    public static long getLatestUndoableActionId(User user)
            throws SQLException {

        if (user == null) {
            return -1;
        }

        String sql =
                "SELECT action_id FROM action_history "
                + "WHERE action_by = ? "
                + "AND action_status = 'APPLIED' "
                + "ORDER BY action_id DESC LIMIT 1";

        try (Connection conn = ConnectDb.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            validateConnection(conn);
            ps.setInt(1, user.getUserId());

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong("action_id") : -1;
            }
        }
    }

    public static long getLatestRedoableActionId(User user)
            throws SQLException {

        if (user == null) {
            return -1;
        }

        /*
         * Redo the oldest undone action first.
         * Example: undo C, then B, then A.
         * Redo must run A, then B, then C.
         */
        String sql =
                "SELECT action_id FROM action_history "
                + "WHERE action_by = ? "
                + "AND action_status = 'UNDONE' "
                + "ORDER BY action_id ASC LIMIT 1";

        try (Connection conn = ConnectDb.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            validateConnection(conn);
            ps.setInt(1, user.getUserId());

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong("action_id") : -1;
            }
        }
    }

    public static boolean undoLast(User user) throws SQLException {
        return changeLastActionStatus(user, true);
    }

    public static boolean redoLast(User user) throws SQLException {
        return changeLastActionStatus(user, false);
    }

    private static boolean changeLastActionStatus(
            User user, boolean undo) throws SQLException {

        validateUser(user);

        String expectedStatus = undo ? "APPLIED" : "UNDONE";
        String newStatus = undo ? "UNDONE" : "APPLIED";

        /*
         * Undo: latest APPLIED action first.
         * Redo: oldest UNDONE action first.
         */
        String order = undo ? "DESC" : "ASC";

        String selectSql =
                "SELECT action_id, module_name, action_type, "
                + "entity_type, entity_id, description, "
                + "before_state, after_state "
                + "FROM action_history "
                + "WHERE action_by = ? AND action_status = ? "
                + "ORDER BY action_id " + order
                + " LIMIT 1 FOR UPDATE";

        try (Connection conn = ConnectDb.getConnection()) {
            validateConnection(conn);
            conn.setAutoCommit(false);

            try {
                HistoryAction action;

                try (PreparedStatement ps =
                        conn.prepareStatement(selectSql)) {

                    ps.setInt(1, user.getUserId());
                    ps.setString(2, expectedStatus);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            conn.rollback();
                            return false;
                        }

                        long rawEntityId = rs.getLong("entity_id");
                        Long entityId = rs.wasNull()
                                ? null : rawEntityId;

                        action = new HistoryAction(
                                rs.getLong("action_id"),
                                rs.getString("module_name"),
                                rs.getString("action_type"),
                                rs.getString("entity_type"),
                                entityId,
                                rs.getString("description"),
                                rs.getString("before_state"),
                                rs.getString("after_state"));
                    }
                }

                String key = action.getModuleName()
                        + ":" + action.getActionType();

                ActionHandler handler = HANDLERS.get(key);

                if (handler == null) {
                    throw new SQLException(
                            "No Undo/Redo handler registered for: "
                            + key);
                }

                if (undo) {
                    handler.undo(conn, action);
                } else {
                    handler.redo(conn, action);
                }

                String updateSql =
                        "UPDATE action_history "
                        + "SET action_status = ?, "
                        + (undo
                            ? "undone_at = CURRENT_TIMESTAMP "
                            : "redone_at = CURRENT_TIMESTAMP ")
                        + "WHERE action_id = ? "
                        + "AND action_by = ? "
                        + "AND action_status = ?";

                try (PreparedStatement ps =
                        conn.prepareStatement(updateSql)) {

                    ps.setString(1, newStatus);
                    ps.setLong(2, action.getActionId());
                    ps.setInt(3, user.getUserId());
                    ps.setString(4, expectedStatus);

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException(
                                "The history status changed unexpectedly.");
                    }
                }

                conn.commit();
                return true;

            } catch (SQLException | RuntimeException ex) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackError) {
                    ex.addSuppressed(rollbackError);
                }
                throw ex;
            } finally {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ignored) {
                    // Connection is closing.
                }
            }
        }
    }

    private static void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException(
                    "A logged-in user is required.");
        }
    }

    private static void validateConnection(Connection conn)
            throws SQLException {

        if (conn == null || conn.isClosed()) {
            throw new SQLException(
                    "A valid database connection is required.");
        }
    }
}

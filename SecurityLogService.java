package com.pos.service;

import com.pos.config.ConnectDb;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SecurityLogService {

    public static boolean logAction(
            String username,
            String role,
            String action,
            String details) {

        String sql = "INSERT INTO security_logs "
                   + "(username, role, action, details, log_date) "
                   + "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";

        if (username == null || role == null) {
            System.err.println(
                "SECURITY LOG FAILED: username or role is null."
            );
            return false;
        }

        try (Connection conn = ConnectDb.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, role);
            ps.setString(3, action);
            ps.setString(4, details);

            int rows = ps.executeUpdate();

            System.out.println(
                "SECURITY LOG RESULT: action=" + action
                + ", rows inserted=" + rows
            );

            return rows > 0;

        } catch (SQLException e) {
            System.err.println(
                "SECURITY LOG DATABASE ERROR: " + e.getMessage()
            );
            e.printStackTrace();
            return false;
        }
    }
}

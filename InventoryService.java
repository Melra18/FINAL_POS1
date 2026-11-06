package com.pos.service;

import com.pos.config.ConnectDb;
import com.pos.model.InventoryBatch;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class InventoryService {
  
        
    public boolean addInventoryBatch(InventoryBatch batch) {
        String sql = "INSERT INTO inventory_batches (product_id, batch_number, date_received, expiration_date, " +
                     "bulk_added, initial_pieces, remaining_pieces, status) VALUES (?, ?, ?, ?, ?, ?, ?, 'Available')";

        try (Connection conn = ConnectDb.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, batch.getProductId());
            pstmt.setString(2, batch.getBatchNumber());
            pstmt.setDate(3, batch.getDateReceived());
            pstmt.setDate(4, batch.getExpirationDate());
            pstmt.setInt(5, batch.getBulkAdded());
            pstmt.setInt(6, batch.getInitialPieces());
            pstmt.setInt(7, batch.getRemainingPieces());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error adding inventory batch: " + e.getMessage());
            return false;
        }
    }

    /**
     * 2. STRICT FIFO DEDUCTION LOGIC
     */
  
/**
 * FIFO deduction using an existing database connection.
 * The caller controls commit() and rollback().
 */
public boolean processFIFODeduction(
              Connection conn,
        int productId,
        int piecesToDeduct) throws SQLException {

    if (piecesToDeduct <= 0) {
        return false;
    }

    String selectSQL =
        "SELECT batch_id, remaining_pieces " +
        "FROM inventory_batches " +
        "WHERE product_id = ? " +
        "AND remaining_pieces > 0 " +
        "AND status = 'Available' " +
        "AND expiration_date >= CURRENT_DATE() " +
        "ORDER BY expiration_date ASC, date_received ASC, batch_id ASC " +
        "FOR UPDATE";

    String updateSQL =
        "UPDATE inventory_batches " +
        "SET remaining_pieces = ?, status = ? " +
        "WHERE batch_id = ?";

    int remainingToDeduct = piecesToDeduct;

    try (PreparedStatement selectStmt =
            conn.prepareStatement(selectSQL)) {

        selectStmt.setInt(1, productId);

        try (ResultSet rs = selectStmt.executeQuery()) {

            while (rs.next() && remainingToDeduct > 0) {

                int batchId = rs.getInt("batch_id");
                int currentPieces = rs.getInt("remaining_pieces");

                int deduction =
                    Math.min(currentPieces, remainingToDeduct);

                int newQty = currentPieces - deduction;

                String newStatus =
                    newQty == 0 ? "Depleted" : "Available";

                try (PreparedStatement updateStmt =
                        conn.prepareStatement(updateSQL)) {

                    updateStmt.setInt(1, newQty);
                    updateStmt.setString(2, newStatus);
                    updateStmt.setInt(3, batchId);

                    if (updateStmt.executeUpdate() == 0) {
                        throw new SQLException(
                            "Failed to update batch ID: " + batchId);
                    }
                }

                remainingToDeduct -= deduction;
            }
        }
    }

    // Stop if eligible stock was insufficient.
    // The caller must roll back the transaction.
    if (remainingToDeduct > 0) {
        return false;
    }

    // Synchronize the product's stock quantity.
    String syncSQL =
        "UPDATE products SET stock_quantity = (" +
        "SELECT COALESCE(SUM(remaining_pieces), 0) " +
        "FROM inventory_batches " +
        "WHERE product_id = ? " +
        "AND remaining_pieces > 0 " +
        "AND status = 'Available' " +
        "AND expiration_date >= CURRENT_DATE()" +
        ") WHERE product_id = ?";

    try (PreparedStatement syncStmt =
            conn.prepareStatement(syncSQL)) {

        syncStmt.setInt(1, productId);
        syncStmt.setInt(2, productId);

        syncStmt.executeUpdate();
    }

    return true;
}

    /**
     * 3. GET TOTAL REMAINING STOCK FOR A PRODUCT
     */
   public int getTotalStockInPieces(int productId) {
    String sql =
        "SELECT COALESCE(SUM(remaining_pieces), 0) AS total_stock " +
        "FROM inventory_batches " +
        "WHERE product_id = ? " +
        "AND remaining_pieces > 0 " +
        "AND status = 'Available' " +
        "AND expiration_date >= CURRENT_DATE()";

    try (Connection conn = ConnectDb.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, productId);

        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total_stock");
            }
        }

    } catch (SQLException e) {
        System.err.println(
            "Error checking stock: " + e.getMessage());
    }

    return 0;
   }
}

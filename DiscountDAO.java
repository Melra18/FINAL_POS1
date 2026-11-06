
package com.pos.dao;

import com.pos.config.ConnectDb;
import com.pos.model.Discount;
import com.pos.model.ConfigurableDiscount;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DiscountDAO {

    private boolean isValidDiscount(String name, double percent) {
        return name != null
                && !name.trim().isEmpty()
                && Double.isFinite(percent)
                && percent >= 0.0
                && percent <= 100.0;
    }

    public List<Discount> getActiveDiscounts() {

        List<Discount> discounts = new ArrayList<>();

        String sql = "SELECT discount_name, discount_percent "
                   + "FROM discounts "
                   + "WHERE status = 'Active' "
                   + "ORDER BY discount_id";

        try (Connection conn = ConnectDb.getConnection()) {

            if (conn == null) {
                System.err.println("DiscountDAO: Database connection unavailable.");
                return discounts;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    String name = rs.getString("discount_name");
                    double percent = rs.getDouble("discount_percent");

                    if (isValidDiscount(name, percent)) {
                        discounts.add(
                            new ConfigurableDiscount(name, percent)
                        );
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Failed to load active discounts.");
            e.printStackTrace();
        }

        return discounts;
    }

    public boolean addDiscount(String name, double percent) {

        if (!isValidDiscount(name, percent)) {
            return false;
        }

        String sql = "INSERT INTO discounts "
                   + "(discount_name, discount_percent, status) "
                   + "VALUES (?, ?, 'Active')";

        try (Connection conn = ConnectDb.getConnection()) {

            if (conn == null) {
                return false;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, name.trim());
                ps.setDouble(2, percent);

                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            System.err.println("Failed to add discount.");
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateDiscount(
            int discountId, String name, double percent) {

        if (discountId <= 0 || !isValidDiscount(name, percent)) {
            return false;
        }

        String sql = "UPDATE discounts "
                   + "SET discount_name = ?, discount_percent = ? "
                   + "WHERE discount_id = ? AND status = 'Active'";

        try (Connection conn = ConnectDb.getConnection()) {

            if (conn == null) {
                return false;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, name.trim());
                ps.setDouble(2, percent);
                ps.setInt(3, discountId);

                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            System.err.println("Failed to update discount.");
            e.printStackTrace();
            return false;
        }
    }

    public boolean archiveDiscount(int discountId) {

        if (discountId <= 0) {
            return false;
        }

        String sql = "UPDATE discounts "
                   + "SET status = 'Archived' "
                   + "WHERE discount_id = ? AND status = 'Active'";

        try (Connection conn = ConnectDb.getConnection()) {

            if (conn == null) {
                return false;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, discountId);

                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            System.err.println("Failed to archive discount.");
            e.printStackTrace();
            return false;
        }
    }
}

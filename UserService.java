package com.pos.service;

import com.pos.config.ConnectDb;
import com.pos.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserService {

    public User authenticate(String username, String password) {

        String sql = "SELECT user_id, username, password, full_name, role, is_archived "
                   + "FROM users "
                   + "WHERE username = ? "
                   + "AND password = ? "
                   + "AND is_archived = 0";

        try (Connection conn = ConnectDb.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username.trim());
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {

                    int userId = rs.getInt("user_id");
                    String dbUsername = rs.getString("username");
                    String dbPassword = rs.getString("password");
                    String fullName = rs.getString("full_name");
                    String role = rs.getString("role");
                    boolean isArchived = rs.getBoolean("is_archived");

                    return new User(
                        userId,
                        dbUsername,
                        dbPassword,
                        fullName,
                        "",
                        "",
                        "",
                        role,
                        1,
                        isArchived
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println("Database Authentication Error: " + e.getMessage());
        }

        return null;
    }
}
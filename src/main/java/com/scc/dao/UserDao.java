package com.scc.dao;

import com.scc.model.user;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    public boolean usernameExists(String username) {
        String sql = "SELECT 1 FROM users WHERE LOWER(username) = LOWER(?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error checking username: " + e.getMessage());
            return false;
        }
    }

    public boolean registerUser(String username, String password, String role, String fullName) {
        if (usernameExists(username)) {
            System.out.println("Username '" + username + "' is already taken. Please choose a different one.");
            return false;
        }

        String sql = """
            INSERT INTO users (username, password_hash, salt, role, account_status, full_name, address, contact_number) 
            VALUES (?, ?, ?, ?, 'ACTIVE', ?, 'Not Provided', 'Not Provided')
        """;
        String salt = SecurityUtil.generateSalt();
        String hashed = SecurityUtil.hashPassword(password, salt);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashed);
            pstmt.setString(3, salt);
            pstmt.setString(4, role.toUpperCase());
            pstmt.setString(5, fullName);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("UNIQUE constraint failed")) {
                System.out.println("Username '" + username + "' is already taken.");
            } else {
                System.err.println("Error registering user: " + e.getMessage());
            }
            return false;
        }
    }

    public user authenticate(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND account_status = 'ACTIVE'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                String storedSalt = rs.getString("salt");

                if (SecurityUtil.verifyPassword(password, storedSalt, storedHash)) {
                    return new user(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            storedHash,
                            rs.getString("role"),
                            rs.getString("account_status"),
                            rs.getString("full_name")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Authentication error: " + e.getMessage());
        }
        return null;
    }

    public List<user> getAllUsers() {
        List<user> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(new user(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("role"),
                        rs.getString("account_status"),
                        rs.getString("full_name")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching users: " + e.getMessage());
        }
        return users;
    }

    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }
}
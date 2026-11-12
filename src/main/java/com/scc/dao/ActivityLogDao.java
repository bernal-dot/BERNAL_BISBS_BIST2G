package com.scc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ActivityLogDao {

    // Keeps 2-parameter compatibility for all UI menus
    public void logActivity(int userId, String action) {
        logActivity(userId, action, action);
    }

    // Overloaded 3-parameter method for detailed logs
    public void logActivity(int userId, String action, String description) {
        String sql = "INSERT INTO activity_logs (user_id, action, description) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, action);
            pstmt.setString(3, description != null ? description : action);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error logging activity: " + e.getMessage());
        }
    }
}
package com.scc.dao;

import com.scc.model.ActivityLog;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogDao {

    // Inserts a new log entry using a parameterized PreparedStatement
    public static void logAction(int userId, String actionType, String description) {
        String sql = "INSERT INTO activity_logs (user_id, action_type, description, timestamp) VALUES (?, ?, ?, datetime('now'))";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, actionType);
            pstmt.setString(3, description);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error logging action: " + e.getMessage());
        }
    }

    // Returns all recorded actions ordered by timestamp DESC
    public List<ActivityLog> getAllLogs() {
        List<ActivityLog> logs = new ArrayList<>();
        String sql = "SELECT log_id, user_id, action_type, description, timestamp FROM activity_logs ORDER BY timestamp DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                logs.add(new ActivityLog(
                        rs.getInt("log_id"),
                        rs.getInt("user_id"),
                        rs.getString("action_type"),
                        rs.getString("description"),
                        rs.getTimestamp("timestamp") // Na-fix: Gi-usab gikan sa getString ngadto sa getTimestamp
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all logs: " + e.getMessage());
        }
        return logs;
    }

    // Returns logs for a specific user ID ordered by timestamp DESC
    public List<ActivityLog> getLogsByUserId(int userId) {
        List<ActivityLog> logs = new ArrayList<>();
        String sql = "SELECT log_id, user_id, action_type, description, timestamp FROM activity_logs WHERE user_id = ? ORDER BY timestamp DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(new ActivityLog(
                            rs.getInt("log_id"),
                            rs.getInt("user_id"),
                            rs.getString("action_type"),
                            rs.getString("description"),
                            rs.getTimestamp("timestamp") // Na-fix: Gi-usab gikan sa getString ngadto sa getTimestamp
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching logs by user ID: " + e.getMessage());
        }
        return logs;
    }

    // Displays the logs neatly in the console
    public void printLogs(List<ActivityLog> logs) {
        if (logs == null || logs.isEmpty()) {
            System.out.println("-> No activity logs found.");
            return;
        }
        System.out.println("\n" + "=".repeat(105));
        System.out.printf("%-5s | %-8s | %-18s | %-35s | %-20s%n",
                "ID", "User ID", "Action Type", "Description", "Timestamp");
        System.out.println("=".repeat(105));

        for (ActivityLog log : logs) {
            System.out.printf("%-5s | %-8s | %-18s | %-35s | %-20s%n",
                    log.getId(),
                    log.getUserId(),
                    log.getActionType(),
                    truncate(log.getDescription(), 35),
                    log.getTimestamp()
            );
        }
        System.out.println("=".repeat(105));
    }

    // Helper method to truncate long descriptions
    private String truncate(String value, int maxLength) {
        if (value == null) {
            return "";
        }
        if (value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength - 3) + "...";
    }
}
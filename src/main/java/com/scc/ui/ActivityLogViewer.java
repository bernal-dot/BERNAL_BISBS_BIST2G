package com.scc.ui;

import com.scc.dao.DatabaseConnection;
import java.sql.*;

public class ActivityLogViewer {

    public void displayLogs() {
        String sql = """
            SELECT al.log_id, u.username, al.action, al.description, al.date_time 
            FROM activity_logs al 
            LEFT JOIN users u ON al.user_id = u.user_id 
            ORDER BY al.date_time DESC 
            LIMIT 50;
        """;

        System.out.println("\n--- RECENT ACTIVITY LOGS ---");
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            boolean hasLogs = false;
            while (rs.next()) {
                hasLogs = true;
                int logId = rs.getInt("log_id");
                String username = rs.getString("username");
                if (username == null) {
                    username = "System/Deleted";
                }
                String action = rs.getString("action");
                String description = rs.getString("description");
                String dateTime = rs.getString("date_time");

                if (description != null && !description.isBlank()) {
                    System.out.printf("[%s] ID: %d | User: %s | Action: %s (%s)%n",
                            dateTime, logId, username, action, description);
                } else {
                    System.out.printf("[%s] ID: %d | User: %s | Action: %s%n",
                            dateTime, logId, username, action);
                }
            }

            if (!hasLogs) {
                System.out.println("No activity logs found.");
            }

        } catch (SQLException e) {
            System.err.println("Error fetching activity logs: " + e.getMessage());
        }
    }
}
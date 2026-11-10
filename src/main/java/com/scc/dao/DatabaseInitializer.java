package com.scc.dao;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseInitializer {
    public static void initialize() {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "username VARCHAR(50) NOT NULL UNIQUE, "
                + "password VARCHAR(255) NOT NULL, "
                + "roles VARCHAR(50) NOT NULL, "
                + "status VARCHAR(50) NOT NULL"
                + ")";

        String createActivityLogsTable = "CREATE TABLE IF NOT EXISTS activity_logs ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "user_id INT, "
                + "action_type VARCHAR(50) NOT NULL, "
                + "description TEXT NOT NULL, "
                + "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY (user_id) REFERENCES users(id)"
                + ")";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createUsersTable);
            stmt.execute(createActivityLogsTable);

            System.out.println("-> Database tables verified and initialized successfully.");

        } catch (SQLException e) {
            System.err.println("-> [ERROR] Failed to initialize database tables: " + e.getMessage());
        }
    }
}
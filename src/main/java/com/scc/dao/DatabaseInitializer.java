package com.scc.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() {
        String createUsersTable = """
            CREATE TABLE IF NOT EXISTS users (
                user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                full_name TEXT NOT NULL,
                address TEXT DEFAULT 'Not Provided',
                contact_number TEXT DEFAULT 'Not Provided',
                username TEXT UNIQUE NOT NULL,
                password_hash TEXT NOT NULL,
                salt TEXT NOT NULL,
                role TEXT NOT NULL,
                account_status TEXT NOT NULL DEFAULT 'ACTIVE'
            );
        """;

        String createServicesTable = """
            CREATE TABLE IF NOT EXISTS services (
                service_id INTEGER PRIMARY KEY AUTOINCREMENT,
                service_name TEXT UNIQUE NOT NULL,
                service_description TEXT,
                service_status TEXT NOT NULL DEFAULT 'AVAILABLE'
            );
        """;

        String createAppointmentsTable = """
            CREATE TABLE IF NOT EXISTS appointments (
                appointment_id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                service_id INTEGER NOT NULL,
                appointment_date TEXT NOT NULL,
                appointment_time TEXT NOT NULL,
                appointment_status TEXT NOT NULL DEFAULT 'PENDING',
                FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                FOREIGN KEY (service_id) REFERENCES services(service_id) ON DELETE CASCADE
            );
        """;

        String createActivityLogsTable = """
            CREATE TABLE IF NOT EXISTS activity_logs (
                log_id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER,
                action TEXT NOT NULL,
                description TEXT,
                date_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL
            );
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createUsersTable);
            stmt.execute(createServicesTable);
            stmt.execute(createAppointmentsTable);
            stmt.execute(createActivityLogsTable);

            // Seed default admin account
            String salt = SecurityUtil.generateSalt();
            String hashedPassword = SecurityUtil.hashPassword("admin123", salt);
            String seedAdmin = String.format("""
                INSERT OR IGNORE INTO users (full_name, address, contact_number, username, password_hash, salt, role, account_status)
                VALUES ('System Administrator', 'Barangay Hall', '09000000000', 'admin', '%s', '%s', 'ADMIN', 'ACTIVE');
            """, hashedPassword, salt);
            stmt.execute(seedAdmin);

            // Seed default barangay services
            stmt.execute("""
                INSERT OR IGNORE INTO services (service_name, service_description, service_status)
                VALUES 
                    ('Barangay Clearance', 'Official clearance issued for employment, business, or legal purposes.', 'AVAILABLE'),
                    ('Certificate of Indigency', 'Certification issued to low-income residents for medical or educational assistance.', 'AVAILABLE'),
                    ('Blotter / Incident Report', 'Formal filing of community disputes or incident records.', 'AVAILABLE');
            """);

        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
        }
    }
}
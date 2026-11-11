package com.scc.dao;

import com.scc.model.user;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserDao {

    public UserDao() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            defaultAdminGenerator(conn);
        } catch (SQLException e) {
            System.err.println("Error seeding default admin: " + e.getMessage());
        }
    }

    public void defaultAdminGenerator(Connection conn) {
        String checkSql = "SELECT COUNT(*) FROM users WHERE roles = 'ADMIN'";
        String insertSql = "INSERT INTO users (username, password, roles, status) VALUES ('admin', 'admin123', 'ADMIN', 'APPROVED')";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkSql)) {
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.executeUpdate(insertSql);
                System.out.println("-> Default Admin created");
            }
        } catch (SQLException e) {
            System.err.println("Error creating default admin:");
            e.printStackTrace();
        }
    }

    public boolean registerUser(String username, String password, String role) {
        String sql = "INSERT INTO users (username, password, roles, status) VALUES (?, ?, ?, 'PENDING')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role.toUpperCase());

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("-> Successful Registration, waiting for Approval");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Registration failed:");
            e.printStackTrace();
        }

        return false;
    }

    public user login(String username, String password) {
        String sql = "SELECT id, username, password, roles, status " +
                "FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String status = rs.getString("status");
                    if ("PENDING".equalsIgnoreCase(status)) {
                        System.out.println("-> Account is pending, waiting for approval");
                        return null;
                    }

                    System.out.println("-> Login successful!");
                    return new user(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("roles"),
                            status
                    );
                } else {
                    System.out.println("-> Invalid username or password.");
                }
            }

            return null;
        } catch (SQLException e) {
            System.err.println("Database error during login:");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<user> getAlluser() {
        List<user> userList = new ArrayList<>();
        String sql = "SELECT id, username, password, roles, status FROM users";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                userList.add(new user(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("roles"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all users: " + e.getMessage());
        }
        return userList;
    }

    public List<user> getPendingUser(){
        List<user> pendingList = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE status = 'PENDING'";

        try(Connection conn = DatabaseConnection.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()){
                pendingList.add(new user(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("roles"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching users: "+e.getMessage());
        }
        return pendingList;
    }

    public boolean approveUser(int userId){
        String sql = "UPDATE users SET status ='APPROVED' WHERE id = ?";

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            int rowsUpdated = pstmt.executeUpdate();

            if(rowsUpdated > 0){
                System.out.println("-> User ID "+userId+"approved successfully!");
                return true;

            }else{
                System.out.println(" -> ID not found. ");
            }
        } catch (Exception e) {
            System.err.println("Error approving users: "+e.getMessage());
        }
        return false;
    }

    public boolean UpdateUserAndStatus (int userId, String newRoles, String newStatus ){

        String sql = "UPDATE users SET roles = ?, status = ? WHERE id = ?";
        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1, newRoles.toUpperCase());
            pstmt.setString(2, newStatus.toUpperCase());
            pstmt.setInt(3, userId);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0){
                System.out.println(" -> User Id:" +userId+ "updated successfully!");
                return true;
            }else{
                System.out.println(" -> User Id not found.");
            }
            return false;
        } catch (SQLException e){
            System.err.println("Error udating user: " +e.getMessage());
        }
        return false;
    }

}
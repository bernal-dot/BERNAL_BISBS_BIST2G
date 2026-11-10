package com.scc.ui;

import com.scc.dao.ActivityLogDao; // Added for Deliverable 8
import com.scc.dao.UserDao;
import com.scc.model.user;
import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private final UserDao userDao;
    private final ActivityLogDao logDao; // Added for Deliverable 8
    private final Scanner scanner;

    public AdminMenu(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.logDao = new ActivityLogDao(); // Initialized ActivityLogDao
        this.scanner = scanner;
    }

    public void display(user admin) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n=================================");
            System.out.println("           ADMIN DASHBOARD         ");
            System.out.println("=================================");
            System.out.println("1. View All Users");
            System.out.println("2. View Pending Users Approvals");
            System.out.println("3. Approve Users Registration");
            System.out.println("4. Update Users Role / Status");
            System.out.println("5. View System Audit Logs"); // Updated from (Reserved)
            System.out.println("6. (Reserved)");
            System.out.println("7. Logout");
            System.out.print("Choose an Option (1-7): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> viewAllUser();
                case "2" -> viewPendingUsers();
                case "3" -> approveUser(admin); // Pass admin to log action
                case "4" -> updateUser(admin);
                case "5" -> viewSystemAuditLogs(); // Added audit log view option
                case "7" -> {
                    System.out.println("-> Logging out...");
                    loggedIn = false;
                }
                default -> System.out.println("-> [ERROR] Invalid Option.");
            }
        }
    }

    private void displayUserTable(List<user> users, String title) {
        System.out.println("\n===================== " + title + " =========================");
        if (users.isEmpty()) {
            System.out.println("-> No Records Found");
            return;
        }

        System.out.println("+-----+-----------------+-----------+-----------+");
        System.out.printf("| %-3s | %-15s | %-10s | %-10s |\n", "ID", "Username", "Roles", "Status");
        System.out.println("+-----+-----------------+-----------+-----------+");

        for (user u : users) {
            System.out.printf("| %-3d | %-15s | %-10s | %-10s |\n",
                    u.id(), u.username(), u.roles(), u.status());
        }

        System.out.println("+-----+-----------------+-----------+-----------+");
    }

    private void viewAllUser() {
        displayUserTable(userDao.getAlluser(), "ALL REGISTERED USERS");
    }

    private void viewPendingUsers() {
        displayUserTable(userDao.getPendingUser(), "PENDING USERS APPROVALS");
    }

    private void approveUser(user currentAdmin) {
        List<user> pendingUser = userDao.getPendingUser();
        displayUserTable(pendingUser, "PENDING APPROVALS");

        if (pendingUser.isEmpty()) {
            return;
        }

        int userId;
        while (true) {
            userId = readIntInput("Enter User ID to approve (or 0 to cancel): ");

            if (userId == 0) {
                System.out.println("-> Operation Cancelled by the user.");
                return;
            }

            int finalUserId = userId;
            boolean exists = pendingUser.stream().anyMatch(u -> u.id() == finalUserId);
            if (exists) {
                break;
            }
            System.out.println("-> [ERROR] User ID not found in pending list. Please try again.");
        }

        userDao.approveUser(userId);

        logDao.logAction(
                currentAdmin.id(),
                "USER_APPROVED",
                "Admin [" + currentAdmin.id() + "] approved account for User ID " + userId
        );

        System.out.println("-> User successfully approved!");
    }

    private void updateUser(user currentAdmin) {
        List<user> allUsers = userDao.getAlluser();
        displayUserTable(allUsers, "SELECT USER TO UPDATE");

        if (allUsers.isEmpty()) {
            return;
        }

        int userId;
        while (true) {
            userId = readIntInput("Enter User ID to update (or 0 to cancel): ");

            if (userId == 0) {
                System.out.println("-> Operation Cancelled.");
                return;
            }

            if (userId == currentAdmin.id()) {
                System.out.println("-> [WARNING] Modifying current active Admin is restricted. Please choose another ID.");
                continue;
            }

            int finalUserId1 = userId;
            boolean exists = allUsers.stream().anyMatch(u -> u.id() == finalUserId1);
            if (exists) {
                break;
            }
            System.out.println("-> [ERROR] User ID not found. Please try again.");
        }

        String newRole = readValidRole();
        if (newRole == null) {
            System.out.println("-> Operation Cancelled.");
            return;
        }

        String newStatus = readValidStatus();
        if (newStatus == null) {
            System.out.println("-> Operation Cancelled.");
            return;
        }

        userDao.UpdateUserAndStatus(userId, newRole, newStatus);

        logDao.logAction(
                currentAdmin.id(),
                "USER_UPDATED",
                "Admin [" + currentAdmin.id() + "] changed role/status of User ID " + userId + " to Role: " + newRole + ", Status: " + newStatus
        );

        System.out.println("-> User successfully updated!");
    }

    private void viewSystemAuditLogs() {
        while (true) {
            System.out.println("\n=== SYSTEM AUDIT LOGS MENU ===");
            System.out.println("1. View All System Logs");
            System.out.println("2. Filter Logs by User ID");
            System.out.println("3. Back to Admin Dashboard");
            System.out.print("Choose an Option (1-3): ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> printLogs(logDao.getAllLogs());
                case "2" -> {
                    int targetUserId = readIntInput("Enter User ID to filter: ");
                    printLogs(logDao.getLogsByUserId(targetUserId));
                }
                case "3" -> {
                    return;
                }
                default -> System.out.println("-> [ERROR] Invalid Option. Please choose 1, 2, or 3.");
            }
        }
    }

    private void printLogs(List<String[]> logs) {
        if (logs.isEmpty()) {
            System.out.println("-> No activity logs found.");
            return;
        }

        System.out.println("\n" + "=".repeat(105));
        // Strict format required: (%-5s | %-8s | %-18s | %-35s | %-20s)
        System.out.printf("%-5s | %-8s | %-18s | %-35s | %-20s%n",
                "ID", "User ID", "Action Type", "Description", "Timestamp");
        System.out.println("=".repeat(105));

        for (String[] log : logs) {
            System.out.printf("%-5s | %-8s | %-18s | %-35s | %-20s%n",
                    log[0], log[1], log[2], truncate(log[3], 35), log[4]);
        }
        System.out.println("=".repeat(105));
    }

    private String truncate(String value, int maxLen) {
        if (value == null) return "";
        return value.length() > maxLen ? value.substring(0, maxLen - 3) + "..." : value;
    }

    private String readValidRole() {
        while (true) {
            System.out.print("Enter Role (STUDENT / TEACHER / ADMIN) [0 to cancel]: ");
            String role = scanner.nextLine().trim().toUpperCase();
            if (role.equals("0") || role.equals("CANCEL") || role.isEmpty()) {
                return null;
            }
            if (role.equals("STUDENT") || role.equals("TEACHER") || role.equals("ADMIN")) {
                return role;
            }
            System.out.println("-> [ERROR] Invalid role. Please enter STUDENT, TEACHER, or ADMIN.");
        }
    }

    private String readValidStatus() {
        while (true) {
            System.out.print("Enter Status (ACTIVE / INACTIVE / PENDING) [0 to cancel]: ");
            String status = scanner.nextLine().trim().toUpperCase();
            if (status.equals("0") || status.equals("CANCEL") || status.isEmpty()) {
                return null;
            }
            if (status.equals("ACTIVE") || status.equals("INACTIVE") || status.equals("PENDING")) {
                return status;
            }
            System.out.println("-> [ERROR] Invalid Status. Please enter ACTIVE, INACTIVE, or PENDING.");
        }
    }

    private int readIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("-> [ERROR] Please enter a valid numeric ID.");
            }
        }
    }
}
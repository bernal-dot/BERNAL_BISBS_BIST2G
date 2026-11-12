package com.scc.ui;

import com.scc.dao.ActivityLogDao;
import com.scc.dao.UserDao;
import com.scc.model.user;
import java.util.Scanner;

public class AdminMenu {
    private final user currentUser;
    private final UserDao userDao = new UserDao();
    private final ActivityLogDao logDao = new ActivityLogDao();
    private final Scanner scanner = new Scanner(System.in);

    public AdminMenu(user currentUser) {
        this.currentUser = currentUser;
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n=== ADMIN DASHBOARD (" + currentUser.fullName() + ") ===");
            System.out.println("1. View All Users");
            System.out.println("2. Create Staff / Captain Account");
            System.out.println("3. Delete User");
            System.out.println("4. View Activity Logs");
            System.out.println("5. Logout");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> {
                    System.out.println("\n--- Registered Users ---");
                    userDao.getAllUsers().forEach(System.out::println);
                }
                case "2" -> createOfficialAccount();
                case "3" -> deleteUserAccount();
                case "4" -> new ActivityLogViewer().displayLogs();
                case "5" -> {
                    logDao.logActivity(currentUser.id(), "Admin logged out");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void createOfficialAccount() {
        System.out.print("Full Name: ");
        String fullName = scanner.nextLine().trim();
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.print("Role (CAPTAIN / STAFF / ADMIN): ");
        String role = scanner.nextLine().trim().toUpperCase();

        if (userDao.registerUser(username, password, role, fullName)) {
            logDao.logActivity(currentUser.id(), "Created " + role + " account: " + username);
            System.out.println("Account created successfully.");
        } else {
            System.out.println("Failed to create account.");
        }
    }

    private void deleteUserAccount() {
        System.out.print("Enter User ID to delete: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            if (id == currentUser.id()) {
                System.out.println("You cannot delete your own active account.");
                return;
            }
            if (userDao.deleteUser(id)) {
                logDao.logActivity(currentUser.id(), "Deleted user ID: " + id);
                System.out.println("User deleted.");
            } else {
                System.out.println("User not found.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format.");
        }
    }
}
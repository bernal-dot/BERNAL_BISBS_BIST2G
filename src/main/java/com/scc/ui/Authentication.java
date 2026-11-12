package com.scc.ui;

import com.scc.dao.ActivityLogDao;
import com.scc.dao.UserDao;
import com.scc.model.user;
import java.util.Scanner;

public class Authentication {
    private final UserDao userDao = new UserDao();
    private final ActivityLogDao logDao = new ActivityLogDao();
    private final Scanner scanner = new Scanner(System.in);

    public void showAuthMenu() {
        while (true) {
            System.out.println("\n=== BARANGAY MANAGEMENT SYSTEM ===");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> handleLogin();
                case "2" -> handleRegister();
                case "3" -> {
                    System.out.println("Exiting system. Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    private void handleLogin() {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        user loggedInUser = userDao.authenticate(username, password);
        if (loggedInUser != null) {
            // Using fullName() instead of username() for greeting
            System.out.println("Welcome, " + loggedInUser.fullName() + "!");
            logDao.logActivity(loggedInUser.id(), "User logged in");
            routeByRole(loggedInUser);
        } else {
            System.out.println("Invalid username, password, or inactive account.");
        }
    }

    private void handleRegister() {
        System.out.print("Full Name: ");
        String fullName = scanner.nextLine().trim();
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        // Pass fullName into the registration method
        if (userDao.registerUser(username, password, "RESIDENT", fullName)) {
            System.out.println("Registration successful! You can now log in.");
        } else {
            System.out.println("Registration failed. Username may already exist.");
        }
    }

    private void routeByRole(user currentUser) {
        switch (currentUser.roles().toUpperCase()) {
            case "ADMIN" -> new AdminMenu(currentUser).showMenu();
            case "CAPTAIN" -> new BarangayCaptainMenu(currentUser).showMenu();
            case "STAFF" -> new BarangayStaffMenu(currentUser).showMenu();
            case "RESIDENT" -> new ResidentMenu(currentUser).showMenu();
            default -> System.out.println("Unknown role assigned to user: " + currentUser.roles());
        }
    }
}
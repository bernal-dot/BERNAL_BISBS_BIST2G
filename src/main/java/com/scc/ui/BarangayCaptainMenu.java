package com.scc.ui;

import com.scc.dao.ActivityLogDao;
import com.scc.model.user;
import java.util.Scanner;

public class BarangayCaptainMenu {
    private final user currentUser;
    private final ActivityLogDao logDao = new ActivityLogDao();
    private final Scanner scanner = new Scanner(System.in);

    public BarangayCaptainMenu(user currentUser) {
        this.currentUser = currentUser;
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n=== BARANGAY CAPTAIN MENU (" + currentUser.fullName() + ") ===");
            System.out.println("1. Review Pending Barangay Clearances");
            System.out.println("2. View Blotter & Incident Reports");
            System.out.println("3. View Activity Logs");
            System.out.println("4. Logout");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> {
                    System.out.println("[Info] Displaying clearances awaiting Captain's signature...");
                    logDao.logActivity(currentUser.id(), "Reviewed pending clearances");
                }
                case "2" -> {
                    System.out.println("[Info] Displaying active blotter reports...");
                    logDao.logActivity(currentUser.id(), "Viewed blotter reports");
                }
                case "3" -> new ActivityLogViewer().displayLogs();
                case "4" -> {
                    logDao.logActivity(currentUser.id(), "Barangay Captain logged out");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }
}
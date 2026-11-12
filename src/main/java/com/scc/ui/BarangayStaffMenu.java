package com.scc.ui;

import com.scc.dao.ActivityLogDao;
import com.scc.model.user;
import java.util.Scanner;

public class BarangayStaffMenu {
    private final user currentUser;
    private final ActivityLogDao logDao = new ActivityLogDao();
    private final Scanner scanner = new Scanner(System.in);

    public BarangayStaffMenu(user currentUser) {
        this.currentUser = currentUser;
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n=== BARANGAY STAFF MENU (" + currentUser.fullName() + ") ===");
            System.out.println("1. Process Resident Certificate Request");
            System.out.println("2. Record Blotter / Incident");
            System.out.println("3. Update Resident Registry");
            System.out.println("4. Logout");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> {
                    System.out.print("Enter Resident Name for Clearance: ");
                    String resident = scanner.nextLine();
                    logDao.logActivity(currentUser.id(), "Processed clearance for " + resident);
                    System.out.println("Clearance queued for Captain's approval.");
                }
                case "2" -> {
                    System.out.print("Enter Incident Summary: ");
                    String incident = scanner.nextLine();
                    logDao.logActivity(currentUser.id(), "Recorded blotter: " + incident);
                    System.out.println("Incident logged.");
                }
                case "3" -> {
                    System.out.println("[Info] Resident registry updated.");
                    logDao.logActivity(currentUser.id(), "Updated resident registry");
                }
                case "4" -> {
                    logDao.logActivity(currentUser.id(), "Barangay Staff logged out");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }
}
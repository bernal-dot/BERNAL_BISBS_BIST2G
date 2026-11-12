package com.scc.ui;

import com.scc.dao.ActivityLogDao;
import com.scc.model.user;
import java.util.Scanner;

public class ResidentMenu {
    private final user currentUser;
    private final ActivityLogDao logDao = new ActivityLogDao();
    private final Scanner scanner = new Scanner(System.in);

    public ResidentMenu(user currentUser) {
        this.currentUser = currentUser;
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n=== RESIDENT PORTAL (" + currentUser.fullName() + ") ===");
            System.out.println("1. Request Barangay Clearance");
            System.out.println("2. Request Certificate of Indigency");
            System.out.println("3. File a Complaint / Blotter");
            System.out.println("4. Logout");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> {
                    System.out.print("Purpose of Clearance: ");
                    String purpose = scanner.nextLine();
                    logDao.logActivity(currentUser.id(), "Requested Barangay Clearance (" + purpose + ")");
                    System.out.println("Request submitted! Please wait for staff processing.");
                }
                case "2" -> {
                    logDao.logActivity(currentUser.id(), "Requested Certificate of Indigency");
                    System.out.println("Certificate of Indigency request submitted.");
                }
                case "3" -> {
                    System.out.print("Describe your concern: ");
                    String concern = scanner.nextLine();
                    logDao.logActivity(currentUser.id(), "Filed resident complaint: " + concern);
                    System.out.println("Your report has been forwarded to Barangay Officials.");
                }
                case "4" -> {
                    logDao.logActivity(currentUser.id(), "Resident logged out");
                    return;
                }
                default -> System.out.println("Invalid option. Please choose between 1 and 4.");
            }
        }
    }
}
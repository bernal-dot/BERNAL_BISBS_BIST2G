package com.scc;

import com.scc.dao.DatabaseInitializer;
import com.scc.ui.Authentication;

public class Main {
    public static void main(String[] args) {
        System.out.println("Barangay Inayagan Service Booking System is running...");

        // 1. Initialize tables and constraints
        DatabaseInitializer.initialize();

        // 2. Launch console interface
        new Authentication().showAuthMenu();
    }
}
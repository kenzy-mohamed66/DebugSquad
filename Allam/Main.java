import models.User;
import data.DataManager;
import UI.DashboardUI;
import UI.LoginUI;
import UI.SignUpUI;

import java.util.Scanner;

/**
 * Main — entry point of the Personal Budgeting System.
 * 
 * Responsibilities:
 *   1. Load all persisted data via DataManager
 *   2. Show welcome screen (Login or Sign Up)
 *   3. On success → launch DashboardUI
 *   4. On exit → save all data
 */
public class Main {

    public static void main(String[] args) {

        // ── Step 1: load persisted data ───────────────────────────────────
        DataManager.loadAll();

        // ── Step 2: shared scanner (one for whole app) ────────────────────
        Scanner scanner = new Scanner(System.in);

        // ── Step 3: welcome loop ──────────────────────────────────────────
        boolean appRunning = true;
        while (appRunning) {
            printWelcome();
            System.out.print("Choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    // Login — US#2
                    LoginUI loginUI = new LoginUI(scanner);
                    User loggedInUser = loginUI.start();
                    if (loggedInUser != null) {
                        // Launch main app hub
                        DashboardUI dashboard = new DashboardUI(scanner, loggedInUser);
                        dashboard.start();
                    }
                }
                case "2" -> {
                    // Sign Up — US#1
                    SignUpUI signUpUI = new SignUpUI(scanner);
                    User newUser = signUpUI.start();
                    if (newUser != null) {
                        // Auto-login after sign up
                        DashboardUI dashboard = new DashboardUI(scanner, newUser);
                        dashboard.start();
                    }
                }
                case "0" -> {
                    appRunning = false;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }

        // ── Step 4: save everything on exit ──────────────────────────────
        DataManager.saveAll();
        System.out.println("\nData saved. Goodbye!");
        scanner.close();
    }

    private static void printWelcome() {
        System.out.println("\n||      PERSONAL BUDGETING SYSTEM       ||");
        System.out.println("||         Cairo University FCAI        ||\n");
        System.out.println("1. Login");
        System.out.println("2. Sign Up");
        System.out.println("0. Exit");
    }
}

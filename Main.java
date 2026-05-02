import Auth.User;
import data.DataManager;
import UI.DashboardUI;
import UI.LoginUI;
import UI.SignUpUI;

import java.util.Scanner;

/**
 * Main — entry point of the Personal Budgeting System.
 *
 * Flow:
 *   1. Load all persisted data via DataManager
 *   2. Welcome screen: Login (US#2) or Sign-Up (US#1)
 *   3. On success → DashboardUI (US#10)
 *   4. On exit → save all data
 */
public class Main {

    public static void main(String[] args) {

        DataManager.loadAll();
        Scanner scanner = new Scanner(System.in);

        boolean appRunning = true;
        while (appRunning) {
            printWelcome();
            System.out.print("Choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    // US#2: Login
                    LoginUI loginUI = new LoginUI(scanner);
                    User user = loginUI.start();
                    if (user != null) {
                        new DashboardUI(scanner, user).start();
                    }
                }
                case "2" -> {
                    // US#1: Sign Up
                    SignUpUI signUpUI = new SignUpUI(scanner);
                    signUpUI.start();
                    User newUser = signUpUI.getRegisteredUser();
                    if (newUser != null) {
                        new DashboardUI(scanner, newUser).start();
                    }
                }
                case "0" -> appRunning = false;
                default  -> System.out.println("Invalid choice.");
            }
        }

        DataManager.saveAll();
        System.out.println("\nData saved. Goodbye!");
        scanner.close();
    }

    private static void printWelcome() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║    PERSONAL BUDGETING SYSTEM           ║");
        System.out.println("║    Cairo University — FCAI             ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("1. Login");
        System.out.println("2. Sign Up");
        System.out.println("0. Exit");
    }
}

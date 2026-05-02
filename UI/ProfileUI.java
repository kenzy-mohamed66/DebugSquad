package UI;

import Auth.User;
import Auth.UserProfile;
import Auth.UserSetting;
import data.DataManager;

import java.util.Scanner;

/**
 * ProfileUI — US#8 sequence diagram.
 *
 * US#8 seq:
 *   displayProfile() → UserProfile.getProfile() → Profile
 *   displaySettings() → UserProfile.changeCurrency()
 *   [Exceptional: Update Failed] → showErrorMessage()
 *   [Normal: Success]            → UserSetting.update() → void
 *   saveChanges()
 */
public class ProfileUI {

    private final Scanner scanner;
    private final User    currentUser;

    public ProfileUI(Scanner scanner, User currentUser) {
        this.scanner     = scanner;
        this.currentUser = currentUser;
    }

    // ─── start() — main loop ─────────────────────────────────────────────────
    public void start() {
        boolean running = true;
        while (running) {
            System.out.println("\n╔══════════════════════════════╗");
            System.out.println("║     PROFILE & SETTINGS       ║");
            System.out.println("╚══════════════════════════════╝");
            System.out.println("1. View Profile");
            System.out.println("2. Settings / Change Currency");
            System.out.println("0. Back");
            System.out.print("Choice: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> displayProfile();   // US#8 seq: displayProfile()
                case "2" -> displaySettings();  // US#8 seq: displaySettings()
                case "0" -> running = false;
                default  -> System.out.println("Invalid choice.");
            }
        }
    }

    // ─── US#8 seq: displayProfile() → UserProfile.getProfile() ───────────────
    public void displayProfile() {
        System.out.println("\n--- Profile ---");
        System.out.println("  UserID: " + currentUser.getUserID());
        System.out.println("  Name  : " + currentUser.getFullName());
        System.out.println("  Email : " + currentUser.getEmail());

        // US#8 seq: UserProfile.getProfile(): Profile
        UserProfile profile = new UserProfile(currentUser.getUserID(), currentUser.getFullName());
        String profileData = profile.getProfile();
        System.out.println(profileData);
    }

    // ─── US#8 seq: displaySettings() → UserProfile.changeCurrency() ───────────
    public void displaySettings() {
        System.out.println("\n--- Settings ---");
        System.out.print("New currency (e.g. EGP, USD, EUR): ");
        String currency = scanner.nextLine().trim();

        if (currency.isBlank()) {
            showErrorMessage(); // US#8 [Exceptional: Update Failed]
            return;
        }

        try {
            // US#8 seq: UserProfile.changeCurrency()
            UserProfile profile = new UserProfile(currentUser.getUserID(), currentUser.getFullName());
            profile.changeCurrency(currency);

            // US#8 seq: [Normal] UserSetting.update() → void
            saveChanges(currency); // US#8 seq: saveChanges()
        } catch (Exception e) {
            showErrorMessage(); // US#8 [Exceptional]
        }
    }

    // ─── US#8 seq: saveChanges() → UserSetting.update() ─────────────────────
    public void saveChanges(String newCurrency) {
        try {
            // US#8 seq: UserSetting.update(): void
            new UserSetting().update();
            System.out.println("Currency preference updated to: " + newCurrency);
        } catch (Exception e) {
            showErrorMessage();
        }
    }

    // Overload with no args (for backward compat)
    public void saveChanges() {
        new UserSetting().update();
        System.out.println("Settings saved.");
    }

    // ─── US#8 seq: showErrorMessage() ────────────────────────────────────────
    public void showErrorMessage() {
        System.out.println("[Profile Error] Update Failed.");
    }
}
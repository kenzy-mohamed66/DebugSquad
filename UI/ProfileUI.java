package UI;

import Auth.User;
import Auth.UserProfile;
import Auth.UserSetting;
import data.DataManager;

import java.util.Scanner;

/**
 * Handles the user interface for profile and settings management.
 *
 * <p>US#8 seq:
 * <ul>
 *   <li>displayProfile() → UserProfile.getProfile() → Profile</li>
 *   <li>displaySettings() → UserProfile.changeCurrency()</li>
 *   <li>[Exceptional: Update Failed] → showErrorMessage()</li>
 *   <li>[Normal: Success]            → UserSetting.update() → void</li>
 *   <li>saveChanges()</li>
 * </ul>
 *
 * @author DebugSquad
 * @version 1.0
 */
public class ProfileUI {

    private final Scanner scanner;
    private final User    currentUser;

    /**
     * Constructs a new {@code ProfileUI}.
     *
     * @param scanner     the scanner for console input
     * @param currentUser the currently logged-in user
     */
    public ProfileUI(Scanner scanner, User currentUser) {
        this.scanner     = scanner;
        this.currentUser = currentUser;
    }

    /**
     * Starts the profile UI loop, allowing viewing or updating settings.
     */
    // ─── start() — main loop ─────────────────────────────────────────────────
    public void start() {
        boolean running = true;
        while (running) {
            System.out.println("\n-----------------------------------");
            System.out.println("        PROFILE & SETTINGS       ");
            System.out.println("-----------------------------------\n");
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

    /**
     * Loads the profile from storage, creating a default one if necessary.
     *
     * @return the active {@link UserProfile}
     */
    // ─── Loads persisted profile, creates default if missing ─────────────────
    private UserProfile loadProfile() {
        UserProfile profile = DataManager.getProfileByUser(currentUser.getUserID());
        if (profile == null) {
            // First time — create and persist
            profile = new UserProfile(currentUser.getUserID(), currentUser.getFullName());
            DataManager.addProfile(profile);
        }
        return profile;
    }

    /**
     * Displays the current user's profile details.
     */
    // ─── US#8 seq: displayProfile() → UserProfile.getProfile() ───────────────
    public void displayProfile() {
        System.out.println("\n--- Profile ---");
        System.out.println("  UserID: " + currentUser.getUserID());
        System.out.println("  Name  : " + currentUser.getFullName());
        System.out.println("  Email : " + currentUser.getEmail());

        // US#8 seq: UserProfile.getProfile(): Profile — from persisted data
        UserProfile profile = loadProfile();
        System.out.println(profile.getProfile());
    }

    /**
     * Prompts the user to change their preferred currency and saves the change.
     */
    // ─── US#8 seq: displaySettings() → UserProfile.changeCurrency() ──────────
    public void displaySettings() {
        System.out.println("\n--- Settings ---");
        System.out.print("New currency (e.g. EGP, USD, EUR): ");
        String currency = scanner.nextLine().trim();

        if (currency.isBlank()) {
            showErrorMessage(); // US#8 [Exceptional: Update Failed]
            return;
        }

        try {
            // Load the real persisted profile
            UserProfile profile = loadProfile();

            // US#8 seq: UserProfile.changeCurrency()
            profile.changeCurrency(currency);

            // Save back to DataManager so it persists
            DataManager.updateProfile(profile);

            // US#8 seq: [Normal] UserSetting.update() → void
            saveChanges(currency); // US#8 seq: saveChanges()
        } catch (Exception e) {
            showErrorMessage(); // US#8 [Exceptional]
        }
    }

    /**
     * Confirm a currency change and triggers a system update notification.
     *
     * @param newCurrency the new currency code
     */
    // ─── US#8 seq: saveChanges() → UserSetting.update() ─────────────────────
    public void saveChanges(String newCurrency) {
        // US#8 seq: UserSetting.update(): void
        new UserSetting().update();
        System.out.println("Currency preference updated to: " + newCurrency);
    }

    /**
     * Generic method to confirm that settings were saved.
     */
    public void saveChanges() {
        new UserSetting().update();
        System.out.println("Settings saved.");
    }

    /**
     * Displays an error message indicating an update failure.
     */
    // ─── US#8 seq: showErrorMessage() ────────────────────────────────────────
    public void showErrorMessage() {
        System.out.println("[Profile Error] Update Failed.");
    }
}
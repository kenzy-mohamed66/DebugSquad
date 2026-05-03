package UI;

import Auth.User;
import Auth.UserProfile;
import data.DataManager;

import java.util.Scanner;

/**
 * LoginUI — US#2 sequence diagram.
 *
 * US#2 seq:
 *   User → LoginUI.displayForm()
 *   LoginUI → User.login(E, P): bool
 *   User    → UserProfile.verifyCredentials(email, password)
 *   [Normal Credentials Valid]  → redirectToDashboard()
 *   [Exceptional Credentials Invalid] → showError("Login failed")
 */
public class LoginUI {

    private final Scanner scanner;
    private User _loggedInUser = null;

    public LoginUI(Scanner scanner) {
        this.scanner = scanner;
    }

    // ─── Entry point called from Main ─────────────────────────────────────────
    public User start() {
        displayForm();
        return _loggedInUser;
    }

    // ─── US#2 seq: displayForm() ──────────────────────────────────────────────
    public void displayForm() {
        System.out.println("\n-----------------------------------");
        System.out.println("            LOGIN             ");
        System.out.println("-----------------------------------\n");

        System.out.print("Email   : ");
        String email = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        // US#2 seq: login(E, P): bool
        boolean success = login(email, password);
        if (success) {
            redirectToDashboard(); // US#2 seq: [Normal] redirectToDashboard()
        } else {
            showError("Login failed"); // US#2 seq: [Exceptional] showError(...)
        }
    }

    // ─── US#2 seq: login(E, P): bool → UserProfile.verifyCredentials() ────────
    public boolean login(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return false;
        }

        User user = DataManager.findUserByEmail(email);
        if (user == null) return false;

        // US#2 seq: User → UserProfile.verifyCredentials(email, password)
        UserProfile profile = new UserProfile(user.getUserID(), user.getFullName());
        boolean credentialsValid = profile.verifyCredentials(email, password);

        if (!credentialsValid) return false;

        // Delegate to User.login()
        boolean loggedIn = user.login(email, password);
        if (loggedIn) _loggedInUser = user;
        return loggedIn;
    }

    // ─── US#2 seq: [Normal] redirectToDashboard() ─────────────────────────────
    public void redirectToDashboard() {
        System.out.println("Welcome back, " + _loggedInUser.getFullName() + "!");
        System.out.println("[Redirecting to Dashboard...]");
    }

    // ─── US#2 seq: [Exceptional] showError("Login failed") ───────────────────
    public void showError(String message) {
        System.out.println("\n[Login Error] " + message);
    }

    public void logout(User user) {
        if (user != null) user.logout();
        _loggedInUser = null;
        System.out.println("Logged out successfully.");
    }
}
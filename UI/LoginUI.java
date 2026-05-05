package UI;

import Auth.User;
import Auth.UserProfile;
import data.DataManager;

import java.util.Scanner;

/**
 * Handles the user interface for the login process.
 *
 * <p>US#2 seq:
 * <ul>
 *   <li>User → LoginUI.displayForm()</li>
 *   <li>LoginUI → User.login(E, P): bool</li>
 *   <li>User    → UserProfile.verifyCredentials(email, password)</li>
 *   <li>[Normal Credentials Valid]  → redirectToDashboard()</li>
 *   <li>[Exceptional Credentials Invalid] → showError("Login failed")</li>
 * </ul>
 *
 * @author DebugSquad
 * @version 1.0
 */
public class LoginUI {

    private final Scanner scanner;
    private User _loggedInUser = null;

    /**
     * Constructs a new {@code LoginUI}.
     *
     * @param scanner the scanner for console input
     */
    public LoginUI(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Starts the login process.
     *
     * @return the logged-in {@link User}, or {@code null} if login fails
     */
    // ─── Entry point called from Main ─────────────────────────────────────────
    public User start() {
        displayForm();
        return _loggedInUser;
    }

    /**
     * Displays the login form and prompts for credentials.
     */
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

    /**
     * Attempts to log in a user with the provided credentials.
     *
     * @param email    the email address entered
     * @param password the password entered
     * @return {@code true} if login is successful
     */
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

    /**
     * Displays a success message upon login.
     */
    // ─── US#2 seq: [Normal] redirectToDashboard() ─────────────────────────────
    public void redirectToDashboard() {
        System.out.println("Welcome back, " + _loggedInUser.getFullName() + "!");
        System.out.println("[Redirecting to Dashboard...]");
    }

    /**
     * Displays an error message upon failed login.
     *
     * @param message the error message to display
     */
    // ─── US#2 seq: [Exceptional] showError("Login failed") ───────────────────
    public void showError(String message) {
        System.out.println("\n[Login Error] " + message);
    }

    /**
     * Logs the current user out.
     *
     * @param user the user to log out
     */
    public void logout(User user) {
        if (user != null) user.logout();
        _loggedInUser = null;
        System.out.println("Logged out successfully.");
    }
}
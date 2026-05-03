package UI;

import Auth.User;
import Auth.UserProfile;
import data.DataManager;

import java.util.Scanner;

/**
 * SignUpUI — US#1 sequence diagram.
 *
 * US#1 seq:
 *   User → SignUpUI.displayForm()
 *   SignUpUI → SignUpUI.validateInput(E, P): bool
 *   SignUpUI → User.register(N, E, P): bool
 *   User → UserProfile.update(): void
 *   [Normal]  → submitRegistration(): void
 *   [Invalid] → showError("Validation failed")
 */
public class SignUpUI {

    private final Scanner scanner;

    public SignUpUI(Scanner scanner) {
        this.scanner = scanner;
    }

    // ─── US#1: entry point (called from Main) ─────────────────────────────────
    public User start() {
        displayForm();
        return null; // form handles its own flow; returns user on success via register()
    }

    // ─── US#1 seq: displayForm() ──────────────────────────────────────────────
    public void displayForm() {
        System.out.println("\n╔══════════════════════════════╗");
        System.out.println("║           SIGN UP            ║");
        System.out.println("╚══════════════════════════════╝");

        System.out.print("Full Name : ");
        String fullName = scanner.nextLine().trim();

        System.out.print("Email     : ");
        String email = scanner.nextLine().trim();

        System.out.print("Password  : ");
        String password = scanner.nextLine().trim();

        System.out.print("Confirm PW: ");
        String confirmPw = scanner.nextLine().trim();

        // US#1 seq: validateInput(E, P): bool
        if (!validateInput(email, password)) {
            showError("Validation failed");
            return;
        }

        if (!password.equals(confirmPw)) {
            showError("Passwords do not match");
            return;
        }

        if (DataManager.findUserByEmail(email) != null) {
            showError("Email already registered");
            return;
        }

        // US#1 seq: register(N, E, P): bool → UserProfile.update(): void
        User user = register(fullName, email, password);
        if (user == null) {
            showError("Validation failed");
        } else {
            submitRegistration(); // US#1 seq: [Normal] submitRegistration(): void
            // Store so Main can get the user back
            _registeredUser = user;
        }
    }

    // Keeps the last registered user so start() can return it
    private User _registeredUser = null;

    @Override
    public String toString() { return "SignUpUI"; }

    // ─── US#1 seq: validateInput(E, P): bool ──────────────────────────────────
    public boolean validateInput(String email, String password) {
        if (email == null || email.isBlank() || !email.contains("@")) return false;
        if (password == null || password.isBlank()) return false;
        return true;
    }

    // ─── US#1 seq: register(N, E, P): bool → UserProfile.update() ─────────────
    public User register(String fullName, String email, String password) {
        User user = new User(fullName, email, password);
        if (!user.register()) return null;

        // US#1 seq: User → UserProfile.update(): void
        UserProfile profile = new UserProfile(user.getUserID(), fullName);
        profile.update(fullName, "EGP", "English");

        DataManager.addUser(user);
        DataManager.addProfile(profile); // persist profile so changes survive
        return user;
    }

    // ─── US#1 seq: [Normal] submitRegistration(): void ────────────────────────
    public void submitRegistration() {
        System.out.println("\nAccount created successfully!");
        System.out.println("You can now log in.");
    }

    // ─── US#1 seq: [Exceptional] showError("Validation failed") ──────────────
    public void showError(String message) {
        System.out.println("\n[SignUp Error] " + message);
    }

    // ─── Used by Main to get the registered user ───────────────────────────────
    public User getRegisteredUser() { return _registeredUser; }
}
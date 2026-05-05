package UI;

import Auth.User;
import Auth.UserProfile;
import data.DataManager;
import java.util.Scanner;

/**
 * Handles the user interface for the registration process.
 *
 * <p>Validates new user data, creates {@link User} and {@link UserProfile} records,
 * and persists them.
 *
 * @author DebugSquad
 * @version 1.0
 */
public class SignUpUI {

    private final Scanner scanner;
    private User _registeredUser = null;

    /**
     * Constructs a new {@code SignUpUI}.
     *
     * @param scanner the scanner for console input
     */
    public SignUpUI(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Starts the registration process.
     *
     * @return {@code null} as registration result is accessed via getter
     */
    public User start() {
        displayForm();
        return null; 
    }

    /**
     * Displays the sign-up form and prompts for user details.
     */
    public void displayForm() {
        System.out.println("\n-----------------------------------");
        System.out.println("        SIGN UP            ");
        System.out.println("-----------------------------------\n");

        System.out.print("Full Name : ");
        String fullName = scanner.nextLine().trim();

        System.out.print("Email     : ");
        String email = scanner.nextLine().trim();

        System.out.print("Password  : ");
        String password = scanner.nextLine().trim();

        System.out.print("Confirm PW: ");
        String confirmPw = scanner.nextLine().trim();
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

        User user = register(fullName, email, password);
        if (user == null) {
            showError("Validation failed");
        } else {
            submitRegistration(); 
            _registeredUser = user;
        }
    }

    /**
     * Returns a string representation of this UI component.
     *
     * @return the string "SignUpUI"
     */
    @Override
    public String toString() { return "SignUpUI"; }

    /**
     * Validates the format of the provided email and password.
     *
     * @param email    the email to check
     * @param password the password to check
     * @return {@code true} if valid
     */
    public boolean validateInput(String email, String password) {
        if (email == null || email.isBlank() || !email.contains("@")) return false;
        if (password == null || password.isBlank()) return false;
        return true;
    }

    /**
     * Creates and registers a new user in the system.
     *
     * @param fullName the new user's full name
     * @param email    the new user's email
     * @param password the new user's password
     * @return the created {@link User}, or {@code null} if registration failed
     */
    public User register(String fullName, String email, String password) {
        User user = new User(fullName, email, password);
        if (!user.register()) return null;

        UserProfile profile = new UserProfile(user.getUserID(), fullName);
        profile.update(fullName, "EGP", "English");

        DataManager.addUser(user);
        DataManager.addProfile(profile); 
        return user;
    }

    /**
     * Displays a success message upon completed registration.
     */
    public void submitRegistration() {
        System.out.println("\nAccount created successfully!");
        System.out.println("You can now log in.");
    }

    /**
     * Displays an error message upon failed registration.
     *
     * @param message the error message to display
     */
    public void showError(String message) {
        System.out.println("\n[SignUp Error] " + message);
    }

    /**
     * Retrieves the newly registered user, if successful.
     *
     * @return the {@link User} object
     */
    public User getRegisteredUser() { return _registeredUser; }
}
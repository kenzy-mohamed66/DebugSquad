package UI;

import Auth.User;
import Auth.UserProfile;
import data.DataManager;

import java.util.Scanner;

public class LoginUI {

    private final Scanner scanner;
    private User _loggedInUser = null;

    public LoginUI(Scanner scanner) {
        this.scanner = scanner;
    }

    public User start() {
        displayForm();
        return _loggedInUser;
    }

    public void displayForm() {
        System.out.println("\n-----------------------------------");
        System.out.println("            LOGIN             ");
        System.out.println("-----------------------------------\n");

        System.out.print("Email   : ");
        String email = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        boolean success = login(email, password);
        if (success) {
            redirectToDashboard();
        } else {
            showError("Login failed");
        }
    }

    public boolean login(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return false;
        }

        User user = DataManager.findUserByEmail(email);
        if (user == null) return false;

        UserProfile profile = new UserProfile(user.getUserID(), user.getFullName());
        boolean credentialsValid = profile.verifyCredentials(email, password);
        if (!credentialsValid) return false;

        boolean loggedIn = user.login(email, password);
        if (loggedIn) _loggedInUser = user;
        return loggedIn;
    }

    public void redirectToDashboard() {
        System.out.println("Welcome back, " + _loggedInUser.getFullName() + "!");
        System.out.println("[Redirecting to Dashboard...]");
    }

    public void showError(String message) {
        System.out.println("\n[Login Error] " + message);
    }

    public void logout(User user) {
        if (user != null) user.logout();
        _loggedInUser = null;
        System.out.println("Logged out successfully.");
    }
}
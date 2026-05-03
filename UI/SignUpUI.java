package UI;

import Auth.User;
import Auth.UserProfile;
import data.DataManager;
import java.util.Scanner;

public class SignUpUI {

    private final Scanner scanner;

    public SignUpUI(Scanner scanner) {
        this.scanner = scanner;
    }

    public User start() {
        displayForm();
        return null; 
    }
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

    private User _registeredUser = null;

    @Override
    public String toString() { return "SignUpUI"; }

    public boolean validateInput(String email, String password) {
        if (email == null || email.isBlank() || !email.contains("@")) return false;
        if (password == null || password.isBlank()) return false;
        return true;
    }

    public User register(String fullName, String email, String password) {
        User user = new User(fullName, email, password);
        if (!user.register()) return null;

        UserProfile profile = new UserProfile(user.getUserID(), fullName);
        profile.update(fullName, "EGP", "English");

        DataManager.addUser(user);
        DataManager.addProfile(profile); 
        return user;
    }

    public void submitRegistration() {
        System.out.println("\nAccount created successfully!");
        System.out.println("You can now log in.");
    }

    public void showError(String message) {
        System.out.println("\n[SignUp Error] " + message);
    }

    public User getRegisteredUser() { return _registeredUser; }
}
package UI;

import data.DataStore;
import models.User;
import models.UserProfile;

public class SignUpUI {

    private final DataStore dataStore = DataStore.getInstance();

    /**
     * Validates all sign-up fields and registers the user.
     * Returns a result object with success flag and message.
     */
    public SignUpResult register(String fullName, String email,
                                 String password, String confirmPassword) {

        // Validate full name
        if (fullName == null || fullName.isBlank())
            return new SignUpResult(false, "Full name cannot be empty.");

        // Validate email format
        if (email == null || !email.contains("@") || !email.contains("."))
            return new SignUpResult(false, "Please enter a valid email address.");

        // Validate password rules
        if (password == null || password.length() < 8)
            return new SignUpResult(false, "Password must be at least 8 characters.");

        if (!hasUpperCase(password))
            return new SignUpResult(false, "Password must contain an uppercase letter.");

        if (!hasDigit(password))
            return new SignUpResult(false, "Password must contain a number.");

        if (!hasSpecial(password))
            return new SignUpResult(false, "Password must contain a special character.");

        // Confirm password match
        if (!password.equals(confirmPassword))
            return new SignUpResult(false, "Passwords do not match.");

        // Email uniqueness
        if (dataStore.emailExists(email))
            return new SignUpResult(false, "An account with this email already exists.");

        // Create and save
        User        user    = new User(fullName, email, password);
        UserProfile profile = new UserProfile(user.getUserID(), fullName);

        if (!user.register())
            return new SignUpResult(false, "Registration failed. Please check your details.");

        dataStore.addUser(user, profile);
        dataStore.setSession(user, profile);

        return new SignUpResult(true, "Account created successfully. Welcome, " + fullName + "!");
    }

    // ── Result wrapper ────────────────────────────────────────────────────────

    public static class SignUpResult {
        public final boolean success;
        public final String  message;
        public SignUpResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private boolean hasUpperCase(String s) { return s.chars().anyMatch(Character::isUpperCase); }
    private boolean hasDigit(String s)     { return s.chars().anyMatch(Character::isDigit); }
    private boolean hasSpecial(String s)   {
        String sp = "!@#$%^&*()_+-=[]{}|;':\",./<>?";
        return s.chars().anyMatch(c -> sp.indexOf(c) >= 0);
    }
}
package service;

import data.DataStore;
import models.User;
import models.UserProfile;

public class LoginService {

    private final DataStore dataStore = DataStore.getInstance();

    /**
     * Validates credentials and opens a session.
     */
    public LoginResult login(String email, String password) {

        if (email == null || email.isBlank())
            return new LoginResult(false, null, "Email cannot be empty.");

        if (!email.contains("@"))
            return new LoginResult(false, null, "Invalid email format.");

        if (password == null || password.isBlank())
            return new LoginResult(false, null, "Password cannot be empty.");

        User user = dataStore.findUser(email, password);

        if (user == null)
            return new LoginResult(false, null, "Invalid email or password. Please try again.");

        UserProfile profile = dataStore.getProfileForUser(user.getUserID());
        dataStore.setSession(user, profile);

        return new LoginResult(true, user, "Login successful. Welcome back, " + user.getFullName() + "!");
    }

    public void logout() {
        User current = dataStore.getCurrentUser();
        if (current != null) current.logout();
        dataStore.clearSession();
    }

    // ── Result wrapper ────────────────────────────────────────────────────────

    public static class LoginResult {
        public final boolean success;
        public final User    user;
        public final String  message;
        public LoginResult(boolean success, User user, String message) {
            this.success = success;
            this.user    = user;
            this.message = message;
        }
    }
}
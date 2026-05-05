package Auth;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents a registered user in the Personal Budgeting System.
 *
 * <p>Stores the user's credentials and account information.
 * Implements {@link Serializable} to allow persistence via {@code DataManager}.
 *
 * @author DebugSquad
 * @version 1.0
 */
public class User implements Serializable {

    private static int idCounter = 1;

    private int           userID;
    private String        fullName;
    private String        password;
    private String        email;
    private LocalDateTime createdAt;

    /**
     * Constructs a new {@code User} with an auto-incremented ID.
     *
     * @param fullName the user's full name
     * @param email    the user's email address
     * @param password the user's password
     */
    public User(String fullName, String email, String password) {
        this.userID    = idCounter++;
        this.fullName  = fullName;
        this.email     = email;
        this.password  = password;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Validates the user's registration data.
     *
     * <p>Checks that the full name, email (must contain {@code @}),
     * and password are all non-blank.
     *
     * @return {@code true} if all fields are valid; {@code false} otherwise
     */
    public boolean register() {
        if (fullName == null || fullName.isBlank()) return false;
        if (email == null || !email.contains("@")) return false;
        if (password == null || password.isBlank()) return false;
        return true;
    }

    /**
     * Authenticates the user against the provided credentials.
     *
     * @param inputEmail    the email address entered by the user
     * @param inputPassword the password entered by the user
     * @return {@code true} if both email and password match; {@code false} otherwise
     */
    public boolean login(String inputEmail, String inputPassword) {
        return this.email.equalsIgnoreCase(inputEmail)
                && this.password.equals(inputPassword);
    }

    /**
     * Logs the user out of the current session.
     *
     * <p>Session cleanup is handled by the calling UI layer.
     */
    public void logout() {
    }

    /** @return the unique user ID */
    public int           getUserID()    { return userID; }
    /** @return the user's full name */
    public String        getFullName()  { return fullName; }
    /** @return the user's email address */
    public String        getEmail()     { return email; }
    /** @return the user's password */
    public String        getPassword()  { return password; }
    /** @return the timestamp when this account was created */
    public LocalDateTime getCreatedAt() { return createdAt; }
    /** @param n the new full name */
    public void setFullName(String n)   { this.fullName = n; }
    /** @param e the new email address */
    public void setEmail(String e)      { this.email = e; }
    /** @param p the new password */
    public void setPassword(String p)   { this.password = p; }
}


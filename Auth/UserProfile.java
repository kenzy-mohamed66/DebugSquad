package Auth;

import java.io.Serializable;

/**
 * Stores the display preferences and profile information for a user.
 *
 * <p>Each {@code UserProfile} is linked to a {@link User} via {@code userID}.
 * It holds settings such as preferred currency, language, and display name.
 * Implements {@link Serializable} to allow persistence via {@code DataManager}.
 *
 * @author DebugSquad
 * @version 1.0
 */
public class UserProfile implements Serializable {

    private int    userID;
    private String displayName;
    private String currency;
    private String language;
    private String profilePicture;

    /**
     * Constructs a new {@code UserProfile} with default settings.
     *
     * <p>Defaults: currency = {@code "EGP"}, language = {@code "English"},
     * profilePicture = {@code ""}.
     *
     * @param userID      the ID of the associated user
     * @param displayName the initial display name
     */
    public UserProfile(int userID, String displayName) {
        this.userID         = userID;
        this.displayName    = displayName;
        this.currency       = "EGP";
        this.language       = "English";
        this.profilePicture = "";
    }

    /**
     * Updates profile fields with the provided values.
     *
     * <p>A field is only changed when the supplied value is non-null and non-blank.
     *
     * @param displayName the new display name (ignored if blank)
     * @param currency    the new preferred currency (ignored if blank)
     * @param language    the new preferred language (ignored if blank)
     */
    public void update(String displayName, String currency, String language) {
        if (displayName != null && !displayName.isBlank()) this.displayName = displayName;
        if (currency    != null && !currency.isBlank())    this.currency    = currency;
        if (language    != null && !language.isBlank())    this.language    = language;
    }

    /**
     * Changes the preferred currency for this profile.
     *
     * @param currency the new currency code (e.g., {@code "USD"})
     */
    public void changeCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * Changes the preferred language for this profile.
     *
     * @param language the new language name (e.g., {@code "Arabic"})
     */
    public void changeLanguage(String language) {
        this.language = language;
    }

    /**
     * Verifies that both email and password are non-blank.
     *
     * <p>Used as a lightweight pre-check before delegating to {@link User#login(String, String)}.
     *
     * @param email    the email address to check
     * @param password the password to check
     * @return {@code true} if both values are non-null and non-blank
     */
    public boolean verifyCredentials(String email, String password) {
        return email != null && !email.isBlank()
                && password != null && !password.isBlank();
    }

    // US#8 seq: displayProfile() → UserProfile.getProfile() → Profile
    /**
     * Returns a formatted string representation of this profile.
     *
     * @return a multi-line string showing name, currency, language, and picture
     */
    public String getProfile() {
        return String.format(
                "  Name    : %s%n  Currency: %s%n  Language: %s%n  Picture : %s",
                displayName, currency, language,
                profilePicture.isEmpty() ? "(none)" : profilePicture);
    }

    /**
     * Returns a string representation of this profile suitable for data export.
     *
     * @return a formatted export string
     */
    public String exportData() {
        return String.format(
                "UserProfile{userID=%d, displayName='%s', currency='%s', language='%s'}",
                userID, displayName, currency, language);
    }

    /** @return the user ID associated with this profile */
    public int    getUserID()         { return userID; }
    /** @return the display name */
    public String getDisplayName()    { return displayName; }
    /** @return the preferred currency code */
    public String getCurrency()       { return currency; }
    /** @return the preferred language */
    public String getLanguage()       { return language; }
    /** @return the profile picture path or URL; empty string if none */
    public String getProfilePicture() { return profilePicture; }
    /** @param n the new display name */
    public void setDisplayName(String n)    { this.displayName = n; }
    /** @param c the new currency code */
    public void setCurrency(String c)       { this.currency = c; }
    /** @param l the new language name */
    public void setLanguage(String l)       { this.language = l; }
    /** @param p the new profile picture value */
    public void setProfilePicture(String p) { this.profilePicture = p; }
}

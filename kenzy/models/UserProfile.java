package models;

public class UserProfile {

    private int    userID;
    private String displayName;
    private String currency;
    private String language;
    private String profilePicture;

    public UserProfile(int userID, String displayName) {
        this.userID         = userID;
        this.displayName    = displayName;
        this.currency       = "EGP";
        this.language       = "English";
        this.profilePicture = "";
    }

    public void update(String displayName, String currency, String language) {
        if (displayName != null && !displayName.isBlank()) this.displayName = displayName;
        if (currency    != null && !currency.isBlank())    this.currency    = currency;
        if (language    != null && !language.isBlank())    this.language    = language;
    }

    public void changeCurrency(String currency) {
        this.currency = currency;
    }

    public void changeLanguage(String language) {
        this.language = language;
    }

    public boolean verifyCredentials(String email, String password) {
        return email != null && !email.isBlank()
                && password != null && password.length() >= 8;
    }

    public String exportData() {
        return String.format(
                "UserProfile{userID=%d, displayName='%s', currency='%s', language='%s'}",
                userID, displayName, currency, language);
    }

    public int    getUserID()         { return userID; }
    public String getDisplayName()    { return displayName; }
    public String getCurrency()       { return currency; }
    public String getLanguage()       { return language; }
    public String getProfilePicture() { return profilePicture; }
    public void setDisplayName(String n)    { this.displayName = n; }
    public void setCurrency(String c)       { this.currency = c; }
    public void setLanguage(String l)       { this.language = l; }
    public void setProfilePicture(String p) { this.profilePicture = p; }
}

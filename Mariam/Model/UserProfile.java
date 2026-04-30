package models;

public class UserProfile {
    private String displayName;
    private String currency;

    public UserProfile(String name, String curr) {
        this.displayName = name;
        this.currency = curr;
    }

    public void changeCurrency(String newCurrency) {
        this.currency = newCurrency;
    }
    
    public String getProfile() {
        return "Name: " + displayName + " | Currency: " + currency;
    }
}

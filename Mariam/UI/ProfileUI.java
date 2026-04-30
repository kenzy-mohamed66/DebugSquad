package ui;
import models.*;
import data.DataManager;

public class ProfileUI {
    public void displayProfile() {
        System.out.println(DataManager.currentUserProfile.getProfile());
    }

    public void saveChanges(String newCurrency) {
        try {
            DataManager.currentUserProfile.changeCurrency(newCurrency);
            new UserSetting().update();
            System.out.println("Success: Currency changed to " + newCurrency);
        } catch (Exception e) {
            System.out.println("Update Failed.");
        }
    }
}
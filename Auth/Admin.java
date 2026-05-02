package Auth;

import java.io.Serializable;

public class Admin implements Serializable {

    private int adminID;

    public Admin(int adminID) {
        this.adminID = adminID;
    }

    public void manageUsers() {
        System.out.println("[Admin] Managing users...");
    }

    public void configureCategories() {
        System.out.println("[Admin] Configuring categories...");
    }

    public void generateSystemReport() {
        System.out.println("[Admin] Generating system report...");
    }

    public int getAdminID() { return adminID; }
}

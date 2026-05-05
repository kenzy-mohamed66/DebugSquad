package Auth;

import java.io.Serializable;

/**
 * Represents an administrator account in the Personal Budgeting System.
 *
 * <p>Administrators have elevated privileges such as managing users
 * and configuring system-wide categories.
 * Implements {@link Serializable} to allow persistence.
 *
 * @author DebugSquad
 * @version 1.0
 */
public class Admin implements Serializable {

    private int adminID;

    /**
     * Constructs a new {@code Admin} with the specified ID.
     *
     * @param adminID the unique identifier for this administrator
     */
    public Admin(int adminID) {
        this.adminID = adminID;
    }

    /**
     * Performs user management operations such as viewing or removing users.
     */
    public void manageUsers() {
        System.out.println("[Admin] Managing users...");
    }

    /**
     * Configures the default and custom transaction categories available in the system.
     */
    public void configureCategories() {
        System.out.println("[Admin] Configuring categories...");
    }

    /**
     * Generates a system-wide report covering all users and transactions.
     */
    public void generateSystemReport() {
        System.out.println("[Admin] Generating system report...");
    }

    /** @return the unique administrator ID */
    public int getAdminID() { return adminID; }
}

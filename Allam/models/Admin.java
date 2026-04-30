package models;

import java.io.Serializable;
// import java.util.List;

/**
 * Admin — privileged user who manages the platform.
 * Does NOT manage personal budgets.
 * From class diagram: manageUsers(), configureCategories(), generateSystemReport()
 */ 
public class Admin implements Serializable {

    // ─── Fields (match class diagram) ───────────────────────────────────────
    private int adminID;

    // ─── Constructor ─────────────────────────────────────────────────────────
    public Admin(int adminID) {
        this.adminID = adminID;
    }

    // ─── Methods from class diagram ──────────────────────────────────────────

    /**
     * Manage user accounts (create, suspend, delete).
     */
    public void manageUsers() {
        // Logic: print all users, offer suspend/delete options
        // Uses DataManager to load/save users
        System.out.println("[Admin] Managing users...");
    }

    /**
     * Configure default income/expense categories for all users.
     */
    public void configureCategories() {
        System.out.println("[Admin] Configuring categories...");
    }

    /**
     * Generate system-wide report to monitor platform usage.
     */
    public void generateSystemReport() {
        System.out.println("[Admin] Generating system report...");
    }

    // ─── Getters ─────────────────────────────────────────────────────────────
    public int getAdminID() { return adminID; }
}

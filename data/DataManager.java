package data;

import Auth.User;
import Auth.UserProfile;
import Budget.Budget;
import Budget.BudgetAlert;
import Goals.FinancialGoal;
import Notifications.Notification;
import Transaction.Category;
import Transaction.Transaction;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all file-based data persistence for the Personal Budgeting System.
 *
 * <p>Uses Java Serialization to save and load lists of objects to/from {@code .dat} files.
 * Acts as the centralized database manager for the application.
 *
 * @author DebugSquad
 * @version 1.0
 */
public class DataManager {

    private static final String DATA_DIR      = "data/";
    private static final String USERS_FILE    = DATA_DIR + "users.dat";
    private static final String TRANS_FILE    = DATA_DIR + "transactions.dat";
    private static final String BUDGETS_FILE  = DATA_DIR + "budgets.dat";
    private static final String GOALS_FILE    = DATA_DIR + "goals.dat";
    private static final String NOTIF_FILE    = DATA_DIR + "notifications.dat";
    private static final String CATEGORY_FILE = DATA_DIR + "categories.dat";
    private static final String ALERTS_FILE   = DATA_DIR + "alerts.dat";
    private static final String PROFILES_FILE = DATA_DIR + "profiles.dat";

    private static List<User>          users         = new ArrayList<>();
    private static List<Transaction>   transactions  = new ArrayList<>();
    private static List<Budget>        budgets       = new ArrayList<>();
    private static List<FinancialGoal> goals         = new ArrayList<>();
    private static List<Notification>  notifications = new ArrayList<>();
    private static List<Category>      categories    = new ArrayList<>();
    private static List<BudgetAlert>   alerts        = new ArrayList<>();
    private static List<UserProfile>   profiles      = new ArrayList<>();

    /** Loads all application data from the file system. */
    public static void loadAll() {
        users         = loadList(USERS_FILE);
        transactions  = loadList(TRANS_FILE);
        budgets       = loadList(BUDGETS_FILE);
        goals         = loadList(GOALS_FILE);
        notifications = loadList(NOTIF_FILE);
        categories    = loadList(CATEGORY_FILE);
        alerts        = loadList(ALERTS_FILE);
        profiles      = loadList(PROFILES_FILE);
        System.out.println("[DataManager] Data loaded successfully.");
        initDefaultCategories();
    }

    /** Saves all current application data to the file system. */
    public static void saveAll() {
        saveList(USERS_FILE,     users);
        saveList(TRANS_FILE,     transactions);
        saveList(BUDGETS_FILE,   budgets);
        saveList(GOALS_FILE,     goals);
        saveList(NOTIF_FILE,     notifications);
        saveList(CATEGORY_FILE,  categories);
        saveList(ALERTS_FILE,    alerts);
        saveList(PROFILES_FILE,  profiles);
    }

    /** Saves the users list to disk. */
    public static void saveUsers()         { saveList(USERS_FILE,    users); }
    /** Saves the transactions list to disk. */
    public static void saveTransactions()  { saveList(TRANS_FILE,    transactions); }
    /** Saves the budgets list to disk. */
    public static void saveBudgets()       { saveList(BUDGETS_FILE,  budgets); }
    /** Saves the goals list to disk. */
    public static void saveGoals()         { saveList(GOALS_FILE,    goals); }
    /** Saves the notifications list to disk. */
    public static void saveNotifications() { saveList(NOTIF_FILE,    notifications); }
    /** Saves the categories list to disk. */
    public static void saveCategories()    { saveList(CATEGORY_FILE, categories); }
    /** Saves the budget alerts list to disk. */
    public static void saveAlerts()        { saveList(ALERTS_FILE,   alerts); }
    /** Saves the user profiles list to disk. */
    public static void saveProfiles()      { saveList(PROFILES_FILE, profiles); }

    /**
     * Loads a serialized list of objects from the specified file.
     *
     * @param filePath the path to the data file
     * @param <T>      the type of objects in the list
     * @return the deserialized list, or an empty list if loading fails
     */
    @SuppressWarnings("unchecked")
    private static <T> List<T> loadList(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<T>) ois.readObject();
        } catch (Exception e) {
            System.out.println("[DataManager] Could not load " + filePath + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Saves a list of objects to the specified file.
     *
     * @param filePath the path to the data file
     * @param list     the list to serialize and save
     * @param <T>      the type of objects in the list
     */
    private static <T> void saveList(String filePath, List<T> list) {
        new File(DATA_DIR).mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(list);
        } catch (Exception e) {
            System.out.println("[DataManager] Could not save " + filePath + ": " + e.getMessage());
        }
    }

    // ─── User ─────────────────────────────────────────────────────────────────
    /** @return the full list of registered users */
    public static List<User> getUsers() { return users; }

    /**
     * Adds a new user and persists the change.
     *
     * @param user the user to add
     */
    public static void addUser(User user) { users.add(user); saveUsers(); }

    /**
     * Finds a user by their email address.
     *
     * @param email the email to search for
     * @return the {@link User}, or {@code null} if not found
     */
    public static User findUserByEmail(String email) {
        return users.stream()
                    .filter(u -> u.getEmail().equalsIgnoreCase(email))
                    .findFirst().orElse(null);
    }

    /**
     * Finds a user by their unique ID.
     *
     * @param id the user ID
     * @return the {@link User}, or {@code null} if not found
     */
    public static User findUserByID(int id) {
        return users.stream()
                    .filter(u -> u.getUserID() == id)
                    .findFirst().orElse(null);
    }

    // ─── UserProfile ──────────────────────────────────────────────────────────
    /** @return the full list of user profiles */
    public static List<UserProfile> getProfiles() { return profiles; }

    /**
     * Adds a new profile and persists the change.
     *
     * @param p the profile to add
     */
    public static void addProfile(UserProfile p) { profiles.add(p); saveProfiles(); }

    /**
     * Retrieves the profile for a specific user ID.
     *
     * @param userID the ID of the user
     * @return the user's {@link UserProfile}, or {@code null} if none exists
     */
    public static UserProfile getProfileByUser(int userID) {
        return profiles.stream()
                       .filter(p -> p.getUserID() == userID)
                       .findFirst().orElse(null);
    }

    /**
     * Updates an existing profile and persists the change.
     *
     * @param updated the updated profile object
     */
    public static void updateProfile(UserProfile updated) {
        for (int i = 0; i < profiles.size(); i++) {
            if (profiles.get(i).getUserID() == updated.getUserID()) {
                profiles.set(i, updated);
                break;
            }
        }
        saveProfiles();
    }

    // ─── Transaction ──────────────────────────────────────────────────────────
    /** @return the full list of all transactions */
    public static List<Transaction> getTransactions() { return transactions; }

    /**
     * Retrieves all transactions associated with a specific user.
     *
     * @param userID the ID of the user
     * @return a list of the user's transactions
     */
    public static List<Transaction> getTransactionsByUser(int userID) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : transactions)
            if (t.getUserID() == userID) result.add(t);
        return result;
    }

    /**
     * Adds a new transaction and persists it.
     *
     * @param t the transaction to add
     */
    public static void addTransaction(Transaction t) { transactions.add(t); saveTransactions(); }

    /**
     * Removes a transaction by its ID and persists the change.
     *
     * @param transactionID the ID of the transaction to remove
     */
    public static void removeTransaction(int transactionID) {
        transactions.removeIf(t -> t.getTransactionID() == transactionID);
        saveTransactions();
    }

    // ─── Budget ───────────────────────────────────────────────────────────────
    /** @return the full list of all budgets */
    public static List<Budget> getBudgets() { return budgets; }

    /**
     * Retrieves all budgets belonging to a specific user.
     *
     * @param userID the ID of the user
     * @return a list of the user's budgets
     */
    public static List<Budget> getBudgetsByUser(int userID) {
        List<Budget> result = new ArrayList<>();
        for (Budget b : budgets)
            if (b.getUserID() == userID) result.add(b);
        return result;
    }

    /**
     * Adds a new budget and persists it.
     *
     * @param b the budget to add
     */
    public static void addBudget(Budget b) { budgets.add(b); saveBudgets(); }

    /**
     * Updates an existing budget and persists the change.
     *
     * @param updated the updated budget object
     */
    public static void updateBudget(Budget updated) {
        for (int i = 0; i < budgets.size(); i++) {
            if (budgets.get(i).getBudgetID() == updated.getBudgetID()) {
                budgets.set(i, updated); break;
            }
        }
        saveBudgets();
    }

    // ─── Goal ─────────────────────────────────────────────────────────────────
    /** @return the full list of all goals */
    public static List<FinancialGoal> getGoals() { return goals; }

    /**
     * Retrieves all financial goals for a specific user.
     *
     * @param userID the ID of the user
     * @return a list of the user's goals
     */
    public static List<FinancialGoal> getGoalsByUser(int userID) {
        List<FinancialGoal> result = new ArrayList<>();
        for (FinancialGoal g : goals)
            if (g.getUserID() == userID) result.add(g);
        return result;
    }

    /**
     * Adds a new goal and persists it.
     *
     * @param g the financial goal to add
     */
    public static void addGoal(FinancialGoal g) { goals.add(g); saveGoals(); }

    /**
     * Updates an existing goal and persists the change.
     *
     * @param updated the updated goal object
     */
    public static void updateGoal(FinancialGoal updated) {
        for (int i = 0; i < goals.size(); i++) {
            if (goals.get(i).getGoalID() == updated.getGoalID()) {
                goals.set(i, updated); break;
            }
        }
        saveGoals();
    }

    // ─── Notification ─────────────────────────────────────────────────────────
    /** @return the full list of system notifications */
    public static List<Notification> getNotifications() { return notifications; }

    /**
     * Retrieves all notifications associated with a specific user.
     *
     * @param userID the ID of the user
     * @return a list of the user's notifications
     */
    public static List<Notification> getNotificationsByUser(int userID) {
        List<Notification> result = new ArrayList<>();
        for (Notification n : notifications)
            if (n.getUserID() == userID) result.add(n);
        return result;
    }

    /**
     * Adds a notification and persists it.
     *
     * @param n the notification to add
     */
    public static void addNotification(Notification n) { notifications.add(n); saveNotifications(); }

    /**
     * Updates an existing notification and persists the change.
     *
     * @param updated the updated notification object
     */
    public static void updateNotification(Notification updated) {
        for (int i = 0; i < notifications.size(); i++) {
            if (notifications.get(i).getNotificationID() == updated.getNotificationID()) {
                notifications.set(i, updated); break;
            }
        }
        saveNotifications();
    }

    // ─── Category ─────────────────────────────────────────────────────────────
    /** @return the full list of all categories */
    public static List<Category> getCategories() { return categories; }

    /**
     * Adds a new category and persists it.
     *
     * @param c the category to add
     */
    public static void addCategory(Category c) { categories.add(c); saveCategories(); }

    // ─── BudgetAlert ──────────────────────────────────────────────────────────
    /** @return the full list of budget alerts */
    public static List<BudgetAlert> getAlerts() { return alerts; }

    /**
     * Adds a budget alert and persists it.
     *
     * @param a the alert to add
     */
    public static void addAlert(BudgetAlert a) { alerts.add(a); saveAlerts(); }

    // ─── Default categories ───────────────────────────────────────────────────
    /** Initializes a default set of categories if none exist. */
    private static void initDefaultCategories() {
        if (!categories.isEmpty()) return;
        String[] defaults = {"Food & Dining", "Transport", "Groceries",
                             "Entertainment", "Bills", "Health", "Education", "Other"};
        for (String name : defaults) categories.add(new Category(name, false));
        saveCategories();
    }
}

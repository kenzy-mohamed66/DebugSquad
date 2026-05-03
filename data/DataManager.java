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

    public static void saveUsers()         { saveList(USERS_FILE,    users); }
    public static void saveTransactions()  { saveList(TRANS_FILE,    transactions); }
    public static void saveBudgets()       { saveList(BUDGETS_FILE,  budgets); }
    public static void saveGoals()         { saveList(GOALS_FILE,    goals); }
    public static void saveNotifications() { saveList(NOTIF_FILE,    notifications); }
    public static void saveCategories()    { saveList(CATEGORY_FILE, categories); }
    public static void saveAlerts()        { saveList(ALERTS_FILE,   alerts); }
    public static void saveProfiles()      { saveList(PROFILES_FILE, profiles); }

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

    private static <T> void saveList(String filePath, List<T> list) {
        new File(DATA_DIR).mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(list);
        } catch (Exception e) {
            System.out.println("[DataManager] Could not save " + filePath + ": " + e.getMessage());
        }
    }

    // ─── User ─────────────────────────────────────────────────────────────────
    public static List<User> getUsers() { return users; }

    public static void addUser(User user) { users.add(user); saveUsers(); }

    public static User findUserByEmail(String email) {
        return users.stream()
                    .filter(u -> u.getEmail().equalsIgnoreCase(email))
                    .findFirst().orElse(null);
    }

    public static User findUserByID(int id) {
        return users.stream()
                    .filter(u -> u.getUserID() == id)
                    .findFirst().orElse(null);
    }

    // ─── UserProfile ──────────────────────────────────────────────────────────
    public static List<UserProfile> getProfiles() { return profiles; }

    public static void addProfile(UserProfile p) { profiles.add(p); saveProfiles(); }

    public static UserProfile getProfileByUser(int userID) {
        return profiles.stream()
                       .filter(p -> p.getUserID() == userID)
                       .findFirst().orElse(null);
    }

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
    public static List<Transaction> getTransactions() { return transactions; }

    public static List<Transaction> getTransactionsByUser(int userID) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : transactions)
            if (t.getUserID() == userID) result.add(t);
        return result;
    }

    public static void addTransaction(Transaction t) { transactions.add(t); saveTransactions(); }

    public static void removeTransaction(int transactionID) {
        transactions.removeIf(t -> t.getTransactionID() == transactionID);
        saveTransactions();
    }

    // ─── Budget ───────────────────────────────────────────────────────────────
    public static List<Budget> getBudgets() { return budgets; }

    public static List<Budget> getBudgetsByUser(int userID) {
        List<Budget> result = new ArrayList<>();
        for (Budget b : budgets)
            if (b.getUserID() == userID) result.add(b);
        return result;
    }

    public static void addBudget(Budget b) { budgets.add(b); saveBudgets(); }

    public static void updateBudget(Budget updated) {
        for (int i = 0; i < budgets.size(); i++) {
            if (budgets.get(i).getBudgetID() == updated.getBudgetID()) {
                budgets.set(i, updated); break;
            }
        }
        saveBudgets();
    }

    // ─── Goal ─────────────────────────────────────────────────────────────────
    public static List<FinancialGoal> getGoals() { return goals; }

    public static List<FinancialGoal> getGoalsByUser(int userID) {
        List<FinancialGoal> result = new ArrayList<>();
        for (FinancialGoal g : goals)
            if (g.getUserID() == userID) result.add(g);
        return result;
    }

    public static void addGoal(FinancialGoal g) { goals.add(g); saveGoals(); }

    public static void updateGoal(FinancialGoal updated) {
        for (int i = 0; i < goals.size(); i++) {
            if (goals.get(i).getGoalID() == updated.getGoalID()) {
                goals.set(i, updated); break;
            }
        }
        saveGoals();
    }

    // ─── Notification ─────────────────────────────────────────────────────────
    public static List<Notification> getNotifications() { return notifications; }

    public static List<Notification> getNotificationsByUser(int userID) {
        List<Notification> result = new ArrayList<>();
        for (Notification n : notifications)
            if (n.getUserID() == userID) result.add(n);
        return result;
    }

    public static void addNotification(Notification n) { notifications.add(n); saveNotifications(); }

    public static void updateNotification(Notification updated) {
        for (int i = 0; i < notifications.size(); i++) {
            if (notifications.get(i).getNotificationID() == updated.getNotificationID()) {
                notifications.set(i, updated); break;
            }
        }
        saveNotifications();
    }

    // ─── Category ─────────────────────────────────────────────────────────────
    public static List<Category> getCategories() { return categories; }

    public static void addCategory(Category c) { categories.add(c); saveCategories(); }

    // ─── BudgetAlert ──────────────────────────────────────────────────────────
    public static List<BudgetAlert> getAlerts() { return alerts; }

    public static void addAlert(BudgetAlert a) { alerts.add(a); saveAlerts(); }

    // ─── Default categories ───────────────────────────────────────────────────
    private static void initDefaultCategories() {
        if (!categories.isEmpty()) return;
        String[] defaults = {"Food & Dining", "Transport", "Groceries",
                             "Entertainment", "Bills", "Health", "Education", "Other"};
        for (String name : defaults) categories.add(new Category(name, false));
        saveCategories();
    }
}

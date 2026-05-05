package data;

import Auth.User;
import Auth.UserProfile;
import Transaction.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An in-memory data store for managing the active session and transactions.
 *
 * <p>Implemented as a Singleton. Used to manage the currently logged-in user
 * and temporarily cache transactions. (Most persistence is handled by {@link DataManager}).
 *
 * @author DebugSquad
 * @version 1.0
 */
public class DataStore {

    private static DataStore instance;

    private final List<User>        users        = new ArrayList<>();
    private final List<UserProfile> profiles     = new ArrayList<>();
    private final List<Transaction> transactions = new ArrayList<>();

    private User        currentUser    = null;
    private UserProfile currentProfile = null;

    private DataStore() {}

    /**
     * Gets the singleton instance of {@code DataStore}.
     *
     * @return the instance
     */
    public static DataStore getInstance() {
        if (instance == null) instance = new DataStore();
        return instance;
    }

    /**
     * Caches a user and their profile.
     *
     * @param u the user
     * @param p the user profile
     */
    public void addUser(User u, UserProfile p) { users.add(u); profiles.add(p); }

    /**
     * Checks if an email is already registered in the store.
     *
     * @param email the email to check
     * @return {@code true} if the email exists
     */
    public boolean emailExists(String email) {
        return users.stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
    }

    /**
     * Attempts to log in a user with the given credentials.
     *
     * @param email    the user's email
     * @param password the user's password
     * @return the matching {@link User}, or {@code null} if not found
     */
    public User findUser(String email, String password) {
        return users.stream()
                .filter(u -> u.login(email, password))
                .findFirst().orElse(null);
    }

    /**
     * Retrieves the profile associated with a user ID.
     *
     * @param userID the ID of the user
     * @return the {@link UserProfile}, or {@code null} if not found
     */
    public UserProfile getProfileForUser(int userID) {
        return profiles.stream()
                .filter(p -> p.getUserID() == userID)
                .findFirst().orElse(null);
    }

    /**
     * Sets the active session for the logged-in user.
     *
     * @param u the logged-in user
     * @param p the user's profile
     */
    public void setSession(User u, UserProfile p) { this.currentUser = u; this.currentProfile = p; }
    
    /** Clears the active session (logout). */
    public void clearSession() { this.currentUser = null; this.currentProfile = null; }

    /** @return {@code true} if a user is currently logged in */
    public boolean     isLoggedIn()        { return currentUser != null; }
    /** @return the currently logged-in user */
    public User        getCurrentUser()    { return currentUser; }
    /** @return the profile of the currently logged-in user */
    public UserProfile getCurrentProfile() { return currentProfile; }

    /**
     * Adds a transaction to the in-memory cache.
     *
     * @param t the transaction to add
     */
    public void addTransaction(Transaction t) { transactions.add(t); }

    /** @return all cached transactions */
    public List<Transaction> getAllTransactions() { return new ArrayList<>(transactions); }

    /**
     * Filters transactions by category.
     *
     * @param category the category name
     * @return a list of matching transactions
     */
    public List<Transaction> filterByCategory(String category) {
        return transactions.stream()
                .filter(t -> t.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    /**
     * Filters transactions by their type (income/expense).
     *
     * @param type the transaction type
     * @return a list of matching transactions
     */
    public List<Transaction> filterByType(Transaction.TransactionType type) {
        return transactions.stream()
                .filter(t -> t.getTypeEnum() == type)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the most recent transactions up to a certain count.
     *
     * @param count the maximum number of transactions to return
     * @return a list of the most recent transactions
     */
    public List<Transaction> getRecentTransactions(int count) {
        List<Transaction> all = getAllTransactions();
        int start = Math.max(0, all.size() - count);
        return all.subList(start, all.size());
    }

    /**
     * Calculates the total balance (income minus expenses).
     *
     * @return the total balance
     */
    public double getTotalBalance() {
        return transactions.stream()
                .mapToDouble(t -> t.getTypeEnum() == Transaction.TransactionType.INCOME
                        ? t.getAmount() : -t.getAmount())
                .sum();
    }
}
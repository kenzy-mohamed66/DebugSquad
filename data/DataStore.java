package data;

import Auth.User;
import Auth.UserProfile;
import Transaction.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataStore {

    private static DataStore instance;

    private final List<User>        users        = new ArrayList<>();
    private final List<UserProfile> profiles     = new ArrayList<>();
    private final List<Transaction> transactions = new ArrayList<>();

    private User        currentUser    = null;
    private UserProfile currentProfile = null;

    private DataStore() {}

    public static DataStore getInstance() {
        if (instance == null) instance = new DataStore();
        return instance;
    }

    public void addUser(User u, UserProfile p) { users.add(u); profiles.add(p); }

    public boolean emailExists(String email) {
        return users.stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
    }

    public User findUser(String email, String password) {
        return users.stream()
                .filter(u -> u.login(email, password))
                .findFirst().orElse(null);
    }

    public UserProfile getProfileForUser(int userID) {
        return profiles.stream()
                .filter(p -> p.getUserID() == userID)
                .findFirst().orElse(null);
    }

    public void setSession(User u, UserProfile p) { this.currentUser = u; this.currentProfile = p; }
    public void clearSession() { this.currentUser = null; this.currentProfile = null; }

    public boolean     isLoggedIn()        { return currentUser != null; }
    public User        getCurrentUser()    { return currentUser; }
    public UserProfile getCurrentProfile() { return currentProfile; }

    public void addTransaction(Transaction t) { transactions.add(t); }

    public List<Transaction> getAllTransactions() { return new ArrayList<>(transactions); }

    public List<Transaction> filterByCategory(String category) {
        return transactions.stream()
                .filter(t -> t.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    public List<Transaction> filterByType(Transaction.TransactionType type) {
        return transactions.stream()
                .filter(t -> t.getTypeEnum() == type)
                .collect(Collectors.toList());
    }

    public List<Transaction> getRecentTransactions(int count) {
        List<Transaction> all = getAllTransactions();
        int start = Math.max(0, all.size() - count);
        return all.subList(start, all.size());
    }

    public double getTotalBalance() {
        return transactions.stream()
                .mapToDouble(t -> t.getTypeEnum() == Transaction.TransactionType.INCOME
                        ? t.getAmount() : -t.getAmount())
                .sum();
    }
}
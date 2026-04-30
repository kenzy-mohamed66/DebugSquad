package service;

import data.DataStore;
import models.Transaction;
import models.Transaction.TransactionType;

import java.time.LocalDateTime;
import java.util.List;

public class TransactionService {

    private final DataStore dataStore = DataStore.getInstance();

    /**
     * Add a new income or expense transaction.
     */
    public TransactionResult addTransaction(double amount, String category,
                                            String description, String notes,
                                            TransactionType type, LocalDateTime dateTime) {

        if (amount <= 0 || amount > 9_999_999.99)
            return new TransactionResult(false, null,
                    "Amount must be greater than 0 and at most 9,999,999.99.");

        if (category == null || category.isBlank())
            return new TransactionResult(false, null, "Category cannot be empty.");

        if (dateTime == null)
            dateTime = LocalDateTime.now();

        Transaction t = new Transaction(amount, category,
                description == null ? "" : description,
                notes == null ? "" : notes,
                type, dateTime);

        if (!t.save())
            return new TransactionResult(false, null, "Failed to save transaction.");

        dataStore.addTransaction(t);

        return new TransactionResult(true, t, "Transaction saved successfully.");
    }

    /**
     * Get all transactions.
     */
    public List<Transaction> getAllTransactions() {
        return dataStore.getAllTransactions();
    }

    /**
     * Filter by category name.
     */
    public List<Transaction> filterByCategory(String category) {
        return dataStore.filterByCategory(category);
    }

    /**
     * Filter by transaction type (INCOME or EXPENSE).
     */
    public List<Transaction> filterByType(TransactionType type) {
        return dataStore.filterByType(type);
    }

    /**
     * Get the last N transactions.
     */
    public List<Transaction> getRecentTransactions(int count) {
        return dataStore.getRecentTransactions(count);
    }

    /**
     * Get the current total balance (income - expenses).
     */
    public double getTotalBalance() {
        return dataStore.getTotalBalance();
    }

    // ── Result wrapper ────────────────────────────────────────────────────────

    public static class TransactionResult {
        public final boolean     success;
        public final Transaction transaction;
        public final String      message;
        public TransactionResult(boolean success, Transaction transaction, String message) {
            this.success     = success;
            this.transaction = transaction;
            this.message     = message;
        }
    }
}
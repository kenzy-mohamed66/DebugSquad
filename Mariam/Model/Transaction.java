package models;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import data.DataManager;

public abstract class Transaction {
    public int transactionID;
    public double amount;
    public LocalDateTime date;
    public String description;
    public String category; // Simplified for console

    public static List<Transaction> getRecentTransactions() {
        return DataManager.allTransactions;
    }

    public static List<Transaction> fetchTransactions(String filter) {
        return DataManager.allTransactions.stream()
                .filter(t -> t.description.contains(filter) || t.category.contains(filter))
                .collect(Collectors.toList());
    }
}
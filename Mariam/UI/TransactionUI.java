package ui;
import models.Transaction;
import java.util.List;

public class TransactionUI {
    public void displayForm() {
        System.out.println("\n--- Transaction History ---");
        List<Transaction> list = Transaction.getRecentTransactions();
        list.forEach(t -> System.out.println(t.description + ": $" + t.amount));
    }

    public void filterTransactions(String filter) {
        List<Transaction> results = Transaction.fetchTransactions(filter);
        if (results.isEmpty()) {
            System.out.println("No matches found.");
        } else {
            System.out.println("Matches found");
            results.forEach(t -> System.out.println(t.description + " | " + t.amount));
        }
    }
}
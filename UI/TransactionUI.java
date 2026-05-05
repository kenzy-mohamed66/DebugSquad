package UI;

import Auth.User;
import Budget.Budget;
import Transaction.Transaction;
import Transaction.Transaction.TransactionType;
import data.DataManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Handles the user interface for transaction management.
 *
 * <p>Allows users to add income, add expenses, and filter/search existing transactions.
 *
 * @author DebugSquad
 * @version 1.0
 */
public class TransactionUI {

    private final Scanner scanner;
    private final User    currentUser;

    /**
     * Constructs a new {@code TransactionUI}.
     *
     * @param scanner     the scanner for console input
     * @param currentUser the currently logged-in user
     */
    public TransactionUI(Scanner scanner, User currentUser) {
        this.scanner     = scanner;
        this.currentUser = currentUser;
    }

    /**
     * Starts the transaction UI loop, showing recent transactions and the main menu.
     */
    public void start() {
        boolean running = true;
        while (running) {
            displayForm(); 
            System.out.println("\n─────────────────────────────");
            System.out.println("1. Add Income");
            System.out.println("2. Add Expense");
            System.out.println("3. Filter / Search");
            System.out.println("0. Back");
            System.out.print("Choice: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> addTransaction(TransactionType.INCOME);
                case "2" -> addTransaction(TransactionType.EXPENSE);
                case "3" -> filterTransactions(); // US#9 seq: filterTransactions()
                case "0" -> running = false;
                default  -> System.out.println("Invalid choice.");
            }
        }
    }

    /**
     * Displays the recent transactions list.
     */
    public void displayForm() {
        System.out.println("\n-----------------------------------");
        System.out.println("        TRANSACTIONS          ");
        System.out.println("-----------------------------------\n");

        List<Transaction> recent = getRecentTransactions();
        if (recent.isEmpty()) {
            System.out.println("  No recent transactions.");
        } else {
            System.out.println("  Recent Transactions:");
            recent.forEach(t -> System.out.println("    " + t.toSummaryString()));
        }
    }

    /**
     * Retrieves the up to 5 most recent transactions for the user.
     *
     * @return a list of recent transactions
     */
    public List<Transaction> getRecentTransactions() {
        List<Transaction> all = DataManager.getTransactionsByUser(currentUser.getUserID());
        int start = Math.max(0, all.size() - 5);
        return all.subList(start, all.size());
    }

    /**
     * Prompts the user to add a new transaction of the specified type.
     *
     * @param type the transaction type (INCOME or EXPENSE)
     */
    private void addTransaction(TransactionType type) {
        System.out.print("Amount: ");
        double amount;
        try { amount = Double.parseDouble(scanner.nextLine().trim()); }
        catch (NumberFormatException e) {
            showError("Please enter valid amount"); // US#3 seq: [Exceptional]
            return;
        }

        if (amount <= 0 || amount > 9_999_999.99) {
            showError("Please enter valid amount"); // US#3 seq: [Exceptional]
            return;
        }

        System.out.print("Category: ");
        String category = scanner.nextLine().trim();
        if (category.isBlank()) { showError("Category cannot be empty"); return; }

        System.out.print("Description (optional): ");
        String description = scanner.nextLine().trim();

        System.out.print("Notes (optional): ");
        String notes = scanner.nextLine().trim();

        Transaction t = new Transaction(
                currentUser.getUserID(), amount, category,
                description, notes, type, LocalDateTime.now());

        boolean saved = t.save();
        if (!saved) { showError("Please enter valid amount"); return; }

        DataManager.addTransaction(t);

        if (type == TransactionType.EXPENSE) {
            updateBudgetForTransaction(category, new BigDecimal(String.valueOf(amount)));
        }

        fetchTransactions("ALL");

        showSuccess(); 
    }

    /**
     * Updates the budget spent amount if the category matches an existing budget.
     *
     * @param category the category name
     * @param amount   the amount to add
     */
    private void updateBudgetForTransaction(String category, BigDecimal amount) {
        List<Budget> budgets = DataManager.getBudgetsByUser(currentUser.getUserID());
        for (Budget b : budgets) {
            if (b.getCategoryName() != null
                    && b.getCategoryName().equalsIgnoreCase(category)) {
                b.updateSpentAmount(amount);
                DataManager.updateBudget(b);

                System.out.printf("  Budget remaining for %s: $%.2f%n",
                        category, b.calcRemaining().doubleValue());
                break;
            }
        }
    }

    /**
     * Prompts the user to filter transactions and displays the result.
     */
    public void filterTransactions() {
        System.out.println("\nFilter by:");
        System.out.println("1. Category");
        System.out.println("2. Type (Income/Expense)");
        System.out.println("3. Show All");
        System.out.print("Choice: ");
        String choice = scanner.nextLine().trim();

        String filter;
        switch (choice) {
            case "1" -> {
                System.out.print("Category name: ");
                filter = "CAT:" + scanner.nextLine().trim();
            }
            case "2" -> {
                System.out.print("Type (IN/EX): ");
                filter = "TYPE:" + scanner.nextLine().trim().toUpperCase();
            }
            default -> filter = "ALL";
        }

        List<Transaction> result = fetchTransactions(filter);

        if (result.isEmpty()) {
            showError("No transactions found for this filter"); // US#9 [Exceptional]
        } else {
            showSuccess("Filter applied successfully"); // US#9 [Normal]
            result.forEach(t -> System.out.println("  " + t));
        }
    }

    /**
     * Fetches and filters the user's transactions based on the query.
     *
     * @param filter the filter string ("ALL", "CAT:[name]", "TYPE:[IN/EX]")
     * @return the filtered list of transactions
     */
    public List<Transaction> fetchTransactions(String filter) {
        List<Transaction> all = DataManager.getTransactionsByUser(currentUser.getUserID());
        if (filter == null || filter.equals("ALL")) return all;
        if (filter.startsWith("CAT:")) {
            String cat = filter.substring(4);
            return all.stream()
                    .filter(t -> t.getCategory().equalsIgnoreCase(cat))
                    .collect(Collectors.toList());
        }
        if (filter.startsWith("TYPE:")) {
            String typStr = filter.substring(5);
            TransactionType type = typStr.equals("IN") ? TransactionType.INCOME : TransactionType.EXPENSE;
            return all.stream()
                    .filter(t -> t.getTypeEnum() == type)
                    .collect(Collectors.toList());
        }
        return all;
    }

    /** Displays a generic success message. */
    public void showSuccess() {
        System.out.println("Transaction saved successfully.");
    }

    /**
     * Displays a customized success message.
     *
     * @param message the message to show
     */
    public void showSuccess(String message) {
        System.out.println(message);
    }

    /**
     * Displays an error message.
     *
     * @param message the error message to show
     */
    public void showError(String message) {
        System.out.println("[Error] " + message);
    }

    /**
     * Retrieves all transactions for the current user.
     *
     * @return a list of transactions
     */
    public List<Transaction> getAllTransactions() {
        return DataManager.getTransactionsByUser(currentUser.getUserID());
    }

    /**
     * Calculates the total balance for the user (income - expenses).
     *
     * @return the total balance
     */
    public double getTotalBalance() {
        return DataManager.getTransactionsByUser(currentUser.getUserID()).stream()
                .mapToDouble(t -> t.getTypeEnum() == TransactionType.INCOME
                        ? t.getAmount() : -t.getAmount())
                .sum();
    }
}
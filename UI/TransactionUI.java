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
 * TransactionUI — US#3 and US#9 sequence diagrams.
 *
 * US#3 seq:
 *   displayForm() → getRecentTransactions() → save()
 *   → Budget.checkThreshold() → fetchTransactions(filter)
 *   [Success] showSuccess()  [Invalid] showError(...)
 *
 * US#5 seq:
 *   displayForm() → Transaction.save() → Budget.updateSpentAmount(amount)
 *   → Budget.checkThreshold() → BudgetAlert.generate() → Notification.create()
 *   [Within Limit] calcRemaining() → showSuccess()
 *
 * US#9 seq:
 *   displayForm() → getRecentTransactions()
 *   filterTransactions() → fetchTransactions(filter)
 *   [Matches] showSuccess(...) → displayForm()
 *   [No Matches] showError(...) → displayForm()
 */
public class TransactionUI {

    private final Scanner scanner;
    private final User    currentUser;

    public TransactionUI(Scanner scanner, User currentUser) {
        this.scanner     = scanner;
        this.currentUser = currentUser;
    }

    // ─── start() — main loop ─────────────────────────────────────────────────
    public void start() {
        boolean running = true;
        while (running) {
            displayForm(); // US#3 & US#9 seq: displayForm()
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

    // ─── US#3 & US#9 seq: displayForm() ──────────────────────────────────────
    public void displayForm() {
        System.out.println("\n╔══════════════════════════════╗");
        System.out.println("║        TRANSACTIONS          ║");
        System.out.println("╚══════════════════════════════╝");

        // US#3 seq: getRecentTransactions(): List<Transaction>
        List<Transaction> recent = getRecentTransactions();
        if (recent.isEmpty()) {
            System.out.println("  No recent transactions.");
        } else {
            System.out.println("  Recent Transactions:");
            recent.forEach(t -> System.out.println("    " + t.toSummaryString()));
        }
    }

    // ─── US#3 & US#9 seq: getRecentTransactions() ────────────────────────────
    public List<Transaction> getRecentTransactions() {
        List<Transaction> all = DataManager.getTransactionsByUser(currentUser.getUserID());
        int start = Math.max(0, all.size() - 5);
        return all.subList(start, all.size());
    }

    // ─── US#3 seq: save() → Budget.checkThreshold() ──────────────────────────
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

        // US#3 seq: save(): void
        boolean saved = t.save();
        if (!saved) { showError("Please enter valid amount"); return; }

        DataManager.addTransaction(t);

        // US#3 & US#5 seq: Budget.checkThreshold() / updateSpentAmount(amount)
        if (type == TransactionType.EXPENSE) {
            updateBudgetForTransaction(category, new BigDecimal(String.valueOf(amount)));
        }

        // US#9 seq: fetchTransactions(filter) — refresh after save
        fetchTransactions("ALL");

        showSuccess(); // US#3 seq: [Normal] showSuccess()
    }

    // ─── US#5 seq: Budget.updateSpentAmount(amount) → checkThreshold() ────────
    private void updateBudgetForTransaction(String category, BigDecimal amount) {
        List<Budget> budgets = DataManager.getBudgetsByUser(currentUser.getUserID());
        for (Budget b : budgets) {
            if (b.getCategoryName() != null
                    && b.getCategoryName().equalsIgnoreCase(category)) {
                // US#5 seq: updateSpentAmount(amount) → checkThreshold() → BudgetAlert.generate()
                b.updateSpentAmount(amount);
                DataManager.updateBudget(b);

                // US#5 seq: [Within Limit] calcRemaining(): Decimal
                System.out.printf("  Budget remaining for %s: $%.2f%n",
                        category, b.calcRemaining().doubleValue());
                break;
            }
        }
    }

    // ─── US#9 seq: filterTransactions() → fetchTransactions(filter) ──────────
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

        // US#9 seq: fetchTransactions(filter)
        List<Transaction> result = fetchTransactions(filter);

        if (result.isEmpty()) {
            showError("No transactions found for this filter"); // US#9 [Exceptional]
        } else {
            showSuccess("Filter applied successfully"); // US#9 [Normal]
            result.forEach(t -> System.out.println("  " + t));
        }
        // Note: loop calls displayForm() at start of next iteration — no duplicate call here
    }

    // ─── US#3 & US#9 seq: fetchTransactions(filter) ──────────────────────────
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

    // ─── US#3 seq: [Normal] showSuccess() ────────────────────────────────────
    public void showSuccess() {
        System.out.println("Transaction saved successfully.");
    }

    public void showSuccess(String message) {
        System.out.println(message);
    }

    // ─── US#3 seq: [Exceptional] showError(msg) ──────────────────────────────
    public void showError(String message) {
        System.out.println("[Error] " + message);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────
    public List<Transaction> getAllTransactions() {
        return DataManager.getTransactionsByUser(currentUser.getUserID());
    }

    public double getTotalBalance() {
        return DataManager.getTransactionsByUser(currentUser.getUserID()).stream()
                .mapToDouble(t -> t.getTypeEnum() == TransactionType.INCOME
                        ? t.getAmount() : -t.getAmount())
                .sum();
    }
}
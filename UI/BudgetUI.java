package UI;

import Auth.User;
import Budget.Budget;
import Transaction.Category;
import data.DataManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

/**
 * BudgetUI — US#4 sequence diagram.
 *
 * US#4 seq:
 *   displayBudgets() → [Displays Budget List]
 *   [Normal Create] createBudget() → Budget.create()
 *     [Category Conflict] Budget.checkThreshold() → showAlert()
 *     [Success]           Budget returns void → showProgressBar()
 *   [Edit Existing]  editBudget()  → Budget.update() → void
 */
public class BudgetUI {

    private final Scanner scanner;
    private final User    currentUser;

    public BudgetUI(Scanner scanner, User currentUser) {
        this.scanner     = scanner;
        this.currentUser = currentUser;
    }

    // ─── start() — main loop ─────────────────────────────────────────────────
    public void start() {
        boolean running = true;
        while (running) {
            displayBudgets(); // US#4 seq: displayBudgets()
            System.out.println("\n─────────────────────────────");
            System.out.println("1. Create Budget");
            System.out.println("2. Edit Budget");
            System.out.println("0. Back");
            System.out.print("Choice: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> createBudget(); // US#4 seq: createBudget()
                case "2" -> editBudget();   // US#4 seq: editBudget()
                case "0" -> running = false;
                default  -> System.out.println("Invalid choice.");
            }
        }
    }

    // ─── US#4 seq: displayBudgets() ───────────────────────────────────────────
    public void displayBudgets() {
        List<Budget> buds = DataManager.getBudgetsByUser(currentUser.getUserID());
        System.out.println("\n-----------------------------------");
        System.out.println("        BUDGETS            ");
        System.out.println("-----------------------------------");
        System.out.println("  Showing all active budgets for current month");
        if (buds.isEmpty()) { System.out.println("  No budgets found."); return; }
        for (Budget b : buds) {
            double limit = b.getLimitAmount() != null ? b.getLimitAmount().doubleValue() : 0;
            double spent = b.getSpentAmount().doubleValue();
            System.out.printf("  [%d] %-18s | Limit: $%.2f | Spent: $%.2f | %s%n",
                    b.getBudgetID(), b.getCategoryName(), limit, spent, b.getStatus());
            showProgressBar(b.getSpentAmount(), b.getLimitAmount()); // show progress inline
        }
    }

    // ─── US#4 seq: createBudget() → Budget.create() ──────────────────────────
    public void createBudget() {
        System.out.print("\nCategory name: ");
        String catName = scanner.nextLine().trim();

        // Check for category conflict (US#4 [Exceptional: Category Conflict])
        List<Budget> existing = DataManager.getBudgetsByUser(currentUser.getUserID());
        boolean conflict = existing.stream()
                .anyMatch(b -> b.getCategoryName().equalsIgnoreCase(catName));
        if (conflict) {
            System.out.println("  [Error: Already Exists]");
            showAlert(catName, BigDecimal.ZERO, BigDecimal.ZERO); // US#4 showAlert()
            return;
        }

        System.out.print("Limit amount : ");
        BigDecimal amount;
        try { amount = new BigDecimal(scanner.nextLine().trim()); }
        catch (NumberFormatException e) { System.out.println("Invalid amount."); return; }

        System.out.print("Alert % (e.g. 80): ");
        int threshold;
        try { threshold = Integer.parseInt(scanner.nextLine().trim()); }
        catch (NumberFormatException e) { System.out.println("Invalid threshold."); return; }

        Budget budget = new Budget();
        budget.setUserID(currentUser.getUserID());
        budget.setLimitAmount(amount);
        budget.setAlertThreshold(threshold);
        budget.setCategoryName(catName);

        // US#4 seq: Budget.create()
        budget.create(); // internally calls checkThreshold()

        DataManager.addBudget(budget);

        // US#4 seq: [Success] showProgressBar()
        showProgressBar(budget.getSpentAmount(), budget.getLimitAmount());
    }

    // ─── US#4 seq: editBudget() → Budget.update() ────────────────────────────
    public void editBudget() {
        List<Budget> buds = DataManager.getBudgetsByUser(currentUser.getUserID());
        if (buds.isEmpty()) { System.out.println("No budgets to edit."); return; }
        displayBudgets();

        System.out.print("Enter Budget ID to edit: ");
        int id;
        try { id = Integer.parseInt(scanner.nextLine().trim()); }
        catch (NumberFormatException e) { System.out.println("Invalid ID."); return; }

        Budget budget = buds.stream()
                .filter(b -> b.getBudgetID() == id).findFirst().orElse(null);
        if (budget == null) { System.out.println("Budget not found."); return; }

        System.out.print("New limit amount: ");
        BigDecimal newAmount;
        try { newAmount = new BigDecimal(scanner.nextLine().trim()); }
        catch (NumberFormatException e) { System.out.println("Invalid amount."); return; }

        System.out.print("New alert % (e.g. 80): ");
        int newThreshold;
        try { newThreshold = Integer.parseInt(scanner.nextLine().trim()); }
        catch (NumberFormatException e) { System.out.println("Invalid threshold."); return; }

        budget.setAmount(newAmount);
        budget.setAlertThreshold(newThreshold);

        // US#4 seq: Budget.update() → void
        budget.update();
        DataManager.updateBudget(budget);
        System.out.println("Budget updated.");
    }

    // ─── US#4 seq: showAlert() ────────────────────────────────────────────────
    public void showAlert(String categoryName, BigDecimal spentAmount, BigDecimal budgetAmount) {
        System.out.println("\n BUDGET ALERT ");
        if (budgetAmount == null || budgetAmount.compareTo(BigDecimal.ZERO) == 0) {
            System.out.println("  Category '" + categoryName + "' already has a budget.");
            return;
        }
        BigDecimal pct = spentAmount.multiply(new BigDecimal(100))
                .divide(budgetAmount, 2, BigDecimal.ROUND_HALF_UP);
        if (pct.compareTo(new BigDecimal(100)) >= 0)
            System.out.printf("  %s: EXCEEDED! Spent $%.2f of $%.2f%n",
                    categoryName, spentAmount.doubleValue(), budgetAmount.doubleValue());
        else
            System.out.printf("  %s: %.0f%% used ($%.2f of $%.2f)%n",
                    categoryName, pct.doubleValue(),
                    spentAmount.doubleValue(), budgetAmount.doubleValue());
    }

    // ─── US#4 seq: showProgressBar() ─────────────────────────────────────────
    public void showProgressBar(BigDecimal spentAmount, BigDecimal totalAmount) {
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) == 0) {
            System.out.println("  Progress: 0% [░░░░░░░░░░]");
            return;
        }
        int pct = spentAmount.multiply(new BigDecimal(100))
                .divide(totalAmount, 0, BigDecimal.ROUND_HALF_UP).intValue();
        if (pct > 100) pct = 100;
        int filled = pct / 10;
        StringBuilder bar = new StringBuilder("  [");
        for (int i = 0; i < 10; i++) bar.append(i < filled ? "█" : "░");
        bar.append("]");
        String icon = pct >= 100 ? "🔴" : (pct >= 80 ? "🟠" : "🟢");
        System.out.println(icon + bar + " " + pct + "%"
                + "  Spent: $" + spentAmount + " / $" + totalAmount);
    }

    // ─── API variants (called from DashboardUI / DataManager code paths) ──────
    public void checkThreshold(Budget budget) {
        if (budget != null) budget.checkThreshold();
    }

    public void update(Budget budget, BigDecimal newAmount, int newAlertThreshold) {
        if (budget == null) { System.out.println("No budget to update"); return; }
        budget.setAmount(newAmount);
        budget.setAlertThreshold(newAlertThreshold);
        budget.update();
        DataManager.updateBudget(budget);
    }

    public void createBudget(Category category, BigDecimal amount, String period, int alertThreshold) {
        if (category == null) { System.out.println("Category cannot be null"); return; }
        Budget budget = new Budget();
        budget.setUserID(currentUser.getUserID());
        budget.setCategory(category);
        budget.setAmount(amount);
        budget.setAlertThreshold(alertThreshold);
        budget.create();
        DataManager.addBudget(budget);
    }
}
package UI;

import Auth.User;
import Budget.Budget;
import Transaction.Category;
import data.DataManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

/**
 * Handles the user interface for budget management.
 *
 * <p>US#4 seq:
 * <ul>
 *   <li>displayBudgets() → [Displays Budget List]</li>
 *   <li>[Normal Create] createBudget() → Budget.create()</li>
 *   <li>[Category Conflict] Budget.checkThreshold() → showAlert()</li>
 *   <li>[Success]           Budget returns void → showProgressBar()</li>
 *   <li>[Edit Existing]  editBudget()  → Budget.update() → void</li>
 * </ul>
 *
 * @author DebugSquad
 * @version 1.0
 */
public class BudgetUI {

    private final Scanner scanner;
    private final User    currentUser;

    /**
     * Constructs a new {@code BudgetUI}.
     *
     * @param scanner     the scanner for console input
     * @param currentUser the currently logged-in user
     */
    public BudgetUI(Scanner scanner, User currentUser) {
        this.scanner     = scanner;
        this.currentUser = currentUser;
    }

    /**
     * Starts the budget UI loop, displaying budgets and the menu.
     */
    // start() — main loop 
    public void start() {
        boolean running = true;
        while (running) {
            displayBudgets(); // US#4 seq: displayBudgets()
            System.out.println("-----------------------------");
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



    /**
     * Prompts the user to create a new budget for a category.
     */
    // story4 seq: createBudget() → Budget.create()
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

        /**
     * Retrieves and displays all active budgets for the current user.
     */
    // story4 seq: displayBudgets()
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

    /**
     * Prompts the user to edit an existing budget's limit and threshold.
     */
    // story4 seq: editBudget() → Budget.update()
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

    /**
     * Displays a warning alert if a budget threshold is exceeded or if a conflict occurs.
     *
     * @param categoryName the budget category
     * @param spentAmount  the amount currently spent
     * @param budgetAmount the total budget limit
     */
    // story4 seq: showAlert() 
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

    /**
     * Renders an ASCII progress bar representing budget usage.
     *
     * @param spentAmount the amount spent so far
     * @param totalAmount the total limit allowed
     */
    // story4 seq: showProgressBar() 
    public void showProgressBar(BigDecimal spentAmount, BigDecimal totalAmount) {
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) == 0) {
            System.out.println("  Progress: 0% [----------]  Spent: $0 / $" + totalAmount);
            return;
        }
        int pct = spentAmount.multiply(new BigDecimal(100))
                .divide(totalAmount, 0, BigDecimal.ROUND_HALF_UP).intValue();
        if (pct > 100) pct = 100;
        int filled = pct / 10;
        StringBuilder bar = new StringBuilder("  [");
        for (int i = 0; i < 10; i++) bar.append(i < filled ? "#" : "-");
        bar.append("] ").append(pct).append("%")
           .append("  Spent: $").append(spentAmount).append(" / $").append(totalAmount);
        String status = pct >= 100 ? "EXCEEDED" : (pct >= 80 ? "WARNING" : "OK");
        System.out.println(bar + "  [" + status + "]");
    }

    /**
     * Checks the threshold for a specific budget directly.
     *
     * @param budget the budget to check
     */
    //  API variants (called from DashboardUI / DataManager code paths) 
    public void checkThreshold(Budget budget) {
        if (budget != null) budget.checkThreshold();
    }

    /**
     * Programmatically updates a budget's limit and threshold.
     *
     * @param budget            the budget to update
     * @param newAmount         the new limit amount
     * @param newAlertThreshold the new alert threshold
     */
    public void update(Budget budget, BigDecimal newAmount, int newAlertThreshold) {
        if (budget == null) { System.out.println("No budget to update"); return; }
        budget.setAmount(newAmount);
        budget.setAlertThreshold(newAlertThreshold);
        budget.update();
        DataManager.updateBudget(budget);
    }

    /**
     * Programmatically creates a budget.
     *
     * @param category       the category object
     * @param amount         the limit amount
     * @param period         the budget period
     * @param alertThreshold the alert threshold percentage
     */
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
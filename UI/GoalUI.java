package UI;

import Auth.User;
import Goals.FinancialGoal;
import data.DataManager;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * Handles the user interface for financial goals management.
 *
 * <p>Allows users to view, create, and contribute to their savings goals.
 *
 * @author DebugSquad
 * @version 1.0
 */
public class GoalUI {

    private final Scanner scanner;
    private final User    currentUser;

    /**
     * Constructs a new {@code GoalUI}.
     *
     * @param scanner     the scanner for console input
     * @param currentUser the currently logged-in user
     */
    public GoalUI(Scanner scanner, User currentUser) {
        this.scanner     = scanner;
        this.currentUser = currentUser;
    }

    /**
     * Starts the goals UI loop, displaying active goals and the main menu.
     */
    //Main loop
    public void start() {
        boolean running = true;

        while (running) {
            displayGoals(); 
            System.out.println("\n─────────────────────────────");
            System.out.println("1. Create Goal");
            System.out.println("2. Contribute to Goal");
            System.out.println("0. Back");
            System.out.print("Choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> addGoal();
                case "2" -> contributeToGoal();
                case "0" -> running = false;
                default  -> System.out.println("Invalid choice.");
            }
        }
    }

    /**
     * Retrieves and displays all active financial goals for the current user.
     */
    public void displayGoals() {

        List<FinancialGoal> goalList = DataManager.getGoalsByUser(currentUser.getUserID());

        System.out.println("\n-----------------------------------");
        System.out.println("        FINANCIAL GOALS       ");
        System.out.println("-----------------------------------\n");
        System.out.println("  Showing all active financial goals");

        if (goalList.isEmpty()) { 
            System.out.println("  No goals found."); 
            return; 
        }

        for (FinancialGoal g : goalList) {
            System.out.printf("  [%d] %-18s | $%.2f / $%.2f | %s%n", g.getGoalID(), g.getName(), g.getCurrentAmount().doubleValue(),
                    g.getTargetAmount().doubleValue(),
                    g.getStatus());
            updateProgress(g); // show inline progress
        }
    }

    /**
     * Prompts the user to create a new financial goal.
     */
    public void addGoal() {

        System.out.print("\nGoal name    : ");

        String name = scanner.nextLine().trim();

        if (name.isBlank()) { 
            System.out.println("Name cannot be empty."); 
            return; 
        }

        System.out.print("Target amount: ");

        BigDecimal target;

        try { target = new BigDecimal(scanner.nextLine().trim()); }
        catch (NumberFormatException e) { 
            System.out.println("Invalid amount."); 
            return; 
        }

        if (target.compareTo(BigDecimal.ZERO) <= 0) { 
            System.out.println("Target must be > 0."); 
            return;
        }

        System.out.print("Deadline (YYYY-MM-DD, or leave blank): ");

        String deadlineStr = scanner.nextLine().trim();
        Date deadline = null;

        if (!deadlineStr.isBlank()) {
            try {
                java.time.LocalDate ld = java.time.LocalDate.parse(deadlineStr);
                deadline = java.sql.Date.valueOf(ld);
                
                // Exceptional Scenario
                if (deadline.before(new Date())) {
                    System.out.println("  [Error: Past Date]");
                    showProgressBar(BigDecimal.ZERO, target);
                    return;
                }
            } 
            catch (Exception e) { 
                System.out.println("Invalid date format.");
                return; 
            }
        }

        FinancialGoal goal = new FinancialGoal();

        goal.setUserID(currentUser.getUserID());
        goal.setName(name);
        goal.setTargetAmount(target);
        goal.setDeadline(deadline);
        //Normal Scenario
        goal.create();
        DataManager.addGoal(goal);
    }

    /**
     * Prompts the user to add a monetary contribution to an existing goal.
     */
    public void contributeToGoal() {

        List<FinancialGoal> goalList = DataManager.getGoalsByUser(currentUser.getUserID());

        if (goalList.isEmpty()) { 
            System.out.println("No goals found.");
            return; 
        }

        displayGoals();

        System.out.print("Enter Goal ID: ");

        int id;

        try { id = Integer.parseInt(scanner.nextLine().trim()); }
        catch (NumberFormatException e) { System.out.println("Invalid ID."); return; }

        FinancialGoal goal = goalList.stream()
                .filter(g -> g.getGoalID() == id).findFirst().orElse(null);

        if (goal == null) { 
            System.out.println("Goal not found."); 
            return; 
        }

        System.out.print("Contribution: ");

        BigDecimal amount;

        try { amount = new BigDecimal(scanner.nextLine().trim()); }
        catch (NumberFormatException e) { 
            System.out.println("Invalid amount."); 
            return; 
        }

        addContribution(goal, amount);
        DataManager.updateGoal(goal);
        updateProgress(goal);
    }

    /**
     * Submits the contribution amount to the specified goal object.
     *
     * @param goal   the target goal
     * @param amount the amount to contribute
     */
    public void addContribution(FinancialGoal goal, BigDecimal amount) {

        if (goal == null || amount == null) { return; }

        goal.addContribution(amount); // internally calls calcRemaining logic

        BigDecimal remaining = goal.calcRemaining();
        System.out.println("  Remaining to save: $" + remaining);
    }

    /**
     * Checks and displays the current completion status of a goal.
     *
     * @param goal the goal to update
     */
    public void updateProgress(FinancialGoal goal) {

        if (goal == null || goal.getTargetAmount() == null || goal.getTargetAmount().compareTo(BigDecimal.ZERO) == 0) { return; }

        BigDecimal pct = goal.getCurrentAmount().multiply(new BigDecimal(100)).divide(goal.getTargetAmount(), 2, BigDecimal.ROUND_HALF_UP);

        showProgressBar(goal.getCurrentAmount(), goal.getTargetAmount());
        System.out.println("  STATUS: " + ("COMPLETED".equals(goal.getStatus()) ? "COMPLETED!" : "IN PROGRESS"));
    }

    /**
     * Renders an ASCII progress bar for the goal.
     *
     * @param current the amount saved so far
     * @param target  the total target amount
     */
    public void showProgressBar(BigDecimal current, BigDecimal target) {

        if (target == null || target.compareTo(BigDecimal.ZERO) == 0) {
            System.out.println("  Progress: [----------] 0%");
            return;
        }

        int pct = current.multiply(new BigDecimal(100))
                .divide(target, 0, BigDecimal.ROUND_HALF_UP).intValue();

        if (pct > 100) { pct = 100; }

        int filled = pct / 10;
        StringBuilder bar = new StringBuilder("  [");

        for (int i = 0; i < 10; i++) { bar.append(i < filled ? "#" : "-"); }

        bar.append("] ").append(pct).append("%");
        System.out.println(bar);
    }

    /**
     * Calculates the remaining amount needed for the goal.
     *
     * @param goal the goal to calculate for
     * @return the remaining amount
     */
    public BigDecimal calcRemaining(FinancialGoal goal) {

        if (goal == null) { return BigDecimal.ZERO; }

        return goal.calcRemaining();
    }

    /**
     * Programmatically creates a goal.
     *
     * @param name         the goal name
     * @param targetAmount the goal target
     * @param deadline     the goal deadline
     * @param initialSaved the initial contribution
     */
    public void createGoal(String name, BigDecimal targetAmount, Date deadline, BigDecimal initialSaved) {

        if (name == null || name.isBlank()) { return; }
        if (targetAmount == null || targetAmount.compareTo(BigDecimal.ZERO) <= 0) { return; }

        FinancialGoal goal = new FinancialGoal();

        goal.setUserID(currentUser.getUserID());
        goal.setName(name);
        goal.setTargetAmount(targetAmount);
        goal.setDeadline(deadline);
        goal.setCurrentAmount(initialSaved != null ? initialSaved : BigDecimal.ZERO);
        goal.create();
        DataManager.addGoal(goal);
    }
}
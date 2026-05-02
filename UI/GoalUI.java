package UI;

import Auth.User;
import Goals.FinancialGoal;
import data.DataManager;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * GoalUI — US#6 sequence diagram.
 *
 * US#6 seq (Create Goal):
 *   displayGoals()
 *   addGoal() → FinancialGoal.create()
 *     [Exceptional: Invalid Date] → showProgressBar() (error state shown)
 *     [Normal]                    → void returned
 *
 * US#6 seq (Track Progress):
 *   contributeToGoal() → FinancialGoal.addContribution(a)
 *                      → FinancialGoal.calcRemaining(): Decimal
 *   GoalUI.updateProgress()
 */
public class GoalUI {

    private final Scanner scanner;
    private final User    currentUser;

    public GoalUI(Scanner scanner, User currentUser) {
        this.scanner     = scanner;
        this.currentUser = currentUser;
    }

    // ─── start() — main loop ─────────────────────────────────────────────────
    public void start() {
        boolean running = true;
        while (running) {
            displayGoals(); // US#6 seq: displayGoals()
            System.out.println("\n─────────────────────────────");
            System.out.println("1. Create Goal");
            System.out.println("2. Contribute to Goal");
            System.out.println("0. Back");
            System.out.print("Choice: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> addGoal();          // US#6 seq: addGoal()
                case "2" -> contributeToGoal(); // US#6 seq: contributeToGoal()
                case "0" -> running = false;
                default  -> System.out.println("Invalid choice.");
            }
        }
    }

    // ─── US#6 seq: displayGoals() ────────────────────────────────────────────
    public void displayGoals() {
        List<FinancialGoal> goalList = DataManager.getGoalsByUser(currentUser.getUserID());
        System.out.println("\n╔══════════════════════════════╗");
        System.out.println("║        FINANCIAL GOALS       ║");
        System.out.println("╚══════════════════════════════╝");
        System.out.println("  Showing all active financial goals");
        if (goalList.isEmpty()) { System.out.println("  No goals found."); return; }
        for (FinancialGoal g : goalList) {
            System.out.printf("  [%d] %-18s | $%.2f / $%.2f | %s%n",
                    g.getGoalID(), g.getName(),
                    g.getCurrentAmount().doubleValue(),
                    g.getTargetAmount().doubleValue(),
                    g.getStatus());
            updateProgress(g); // show inline progress
        }
    }

    // ─── US#6 seq (Create Goal): addGoal() → FinancialGoal.create() ──────────
    public void addGoal() {
        System.out.print("\nGoal name    : ");
        String name = scanner.nextLine().trim();
        if (name.isBlank()) { System.out.println("Name cannot be empty."); return; }

        System.out.print("Target amount: ");
        BigDecimal target;
        try { target = new BigDecimal(scanner.nextLine().trim()); }
        catch (NumberFormatException e) { System.out.println("Invalid amount."); return; }

        if (target.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Target must be > 0."); return;
        }

        System.out.print("Deadline (YYYY-MM-DD, or leave blank): ");
        String deadlineStr = scanner.nextLine().trim();
        Date deadline = null;
        if (!deadlineStr.isBlank()) {
            try {
                java.time.LocalDate ld = java.time.LocalDate.parse(deadlineStr);
                deadline = java.sql.Date.valueOf(ld);
                // US#6 [Exceptional: Invalid Date] — past date check
                if (deadline.before(new Date())) {
                    System.out.println("  [Error: Past Date]");
                    showProgressBar(BigDecimal.ZERO, target); // US#6 showProgressBar() in error state
                    return;
                }
            } catch (Exception e) {
                System.out.println("Invalid date format."); return;
            }
        }

        FinancialGoal goal = new FinancialGoal();
        goal.setUserID(currentUser.getUserID());
        goal.setName(name);
        goal.setTargetAmount(target);
        goal.setDeadline(deadline);

        // US#6 seq: FinancialGoal.create() → [Normal] void
        goal.create();
        DataManager.addGoal(goal);
    }

    // ─── US#6 seq (Track): contributeToGoal() → addContribution(a) ───────────
    public void contributeToGoal() {
        List<FinancialGoal> goalList = DataManager.getGoalsByUser(currentUser.getUserID());
        if (goalList.isEmpty()) { System.out.println("No goals found."); return; }
        displayGoals();

        System.out.print("Enter Goal ID: ");
        int id;
        try { id = Integer.parseInt(scanner.nextLine().trim()); }
        catch (NumberFormatException e) { System.out.println("Invalid ID."); return; }

        FinancialGoal goal = goalList.stream()
                .filter(g -> g.getGoalID() == id).findFirst().orElse(null);
        if (goal == null) { System.out.println("Goal not found."); return; }

        System.out.print("Contribution: ");
        BigDecimal amount;
        try { amount = new BigDecimal(scanner.nextLine().trim()); }
        catch (NumberFormatException e) { System.out.println("Invalid amount."); return; }

        // US#6 seq: FinancialGoal.addContribution(a) → calcRemaining(): Decimal
        addContribution(goal, amount);

        DataManager.updateGoal(goal);

        // US#6 seq: GoalUI.updateProgress()
        updateProgress(goal);
    }

    // ─── US#6 seq: addContribution(a) → calcRemaining() ─────────────────────
    public void addContribution(FinancialGoal goal, BigDecimal amount) {
        if (goal == null || amount == null) return;
        goal.addContribution(amount); // internally calls calcRemaining logic
        BigDecimal remaining = goal.calcRemaining(); // US#6 seq: calcRemaining(): Decimal
        System.out.println("  Remaining to save: $" + remaining);
    }

    // ─── US#6 seq: updateProgress() ──────────────────────────────────────────
    public void updateProgress(FinancialGoal goal) {
        if (goal == null || goal.getTargetAmount() == null
                || goal.getTargetAmount().compareTo(BigDecimal.ZERO) == 0) return;

        BigDecimal pct = goal.getCurrentAmount()
                .multiply(new BigDecimal(100))
                .divide(goal.getTargetAmount(), 2, BigDecimal.ROUND_HALF_UP);

        showProgressBar(goal.getCurrentAmount(), goal.getTargetAmount());

        System.out.println("  STATUS: "
                + ("COMPLETED".equals(goal.getStatus()) ? "COMPLETED!" : "IN PROGRESS"));
    }

    // ─── US#6 seq: showProgressBar() ─────────────────────────────────────────
    public void showProgressBar(BigDecimal current, BigDecimal target) {
        if (target == null || target.compareTo(BigDecimal.ZERO) == 0) {
            System.out.println("  [░░░░░░░░░░] 0%");
            return;
        }
        int pct = current.multiply(new BigDecimal(100))
                .divide(target, 0, BigDecimal.ROUND_HALF_UP).intValue();
        if (pct > 100) pct = 100;
        int filled = pct / 10;
        StringBuilder bar = new StringBuilder("  [");
        for (int i = 0; i < 10; i++) bar.append(i < filled ? "█" : "░");
        bar.append("] ").append(pct).append("%");
        System.out.println(bar);
    }

    // ─── API variants ─────────────────────────────────────────────────────────
    public BigDecimal calcRemaining(FinancialGoal goal) {
        if (goal == null) return BigDecimal.ZERO;
        return goal.calcRemaining();
    }

    public void createGoal(String name, BigDecimal targetAmount, Date deadline, BigDecimal initialSaved) {
        if (name == null || name.isBlank()) return;
        if (targetAmount == null || targetAmount.compareTo(BigDecimal.ZERO) <= 0) return;
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
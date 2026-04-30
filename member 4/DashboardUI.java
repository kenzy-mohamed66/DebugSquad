package ui;

import auth.User;
import budget.Budget;
import budget.BudgetAlert;
import data.DataManager;
import goals.FinancialGoal;
import notifications.Notification;
import reports.Report;
import transaction.Transaction;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/** 
 * DashboardUI — «boundary» class for US#10 Dashboard Overview.
 * Also acts as the MAIN NAVIGATION HUB after login.
 *
 * Sequence diagram interactions implemented:
 *   refreshData(): void
 *   generate(startDate, endDate): Report  → Report.getIncomeVsExpense()
 *                                         → Report.getCategoryBreakdown()
 *   loadRecentTransactions(): void        → Transaction.getRecentTransactions()
 *   checkThreshold(): void                → Budget.checkThreshold()
 *                                         → BudgetAlert.generate()
 *                                         → Notification.create(type, message)
 *   showBudgetWarning(): void
 *   showGoalsSummary(): void
 *   displayDashboard(): void
 */
public class DashboardUI {

    private Scanner  scanner;
    private User     currentUser;

    // ─── Other UI screens (hub pattern) ──────────────────────────────────────
    private TransactionUI  transactionUI;
    private BudgetUI       budgetUI;
    private GoalUI         goalUI;
    private ReportUI       reportUI;
    private ProfileUI      profileUI;
    private NotificationUI notificationUI;
    private ExportUI       exportUI;

    public DashboardUI(Scanner scanner, User currentUser) {
        this.scanner     = scanner;
        this.currentUser = currentUser;

        // Instantiate all sub-UIs with shared scanner and current user
        this.transactionUI  = new TransactionUI(scanner, currentUser);
        this.budgetUI        = new BudgetUI(scanner, currentUser);
        this.goalUI          = new GoalUI(scanner, currentUser);
        this.reportUI        = new ReportUI(scanner, currentUser);
        this.profileUI       = new ProfileUI(scanner, currentUser);
        this.notificationUI  = new NotificationUI(scanner, currentUser);
        this.exportUI        = new ExportUI(scanner, currentUser);
    }

    // ─── Main app loop ────────────────────────────────────────────────────────
    public void start() {
        refreshData(); // seq diagram: first thing on dashboard load

        boolean running = true;
        while (running) {
            displayDashboard();
            System.out.println("\n─────────────────────────────");
            System.out.println("1.  Transactions");
            System.out.println("2.  Budgets");
            System.out.println("3.  Goals");
            System.out.println("4.  Reports");
            System.out.println("5.  Profile & Settings");
            System.out.println("6.  Notifications");
            System.out.println("7.  Export Data");
            System.out.println("8.  Refresh");
            System.out.println("0.  Logout");
            System.out.print("Choice: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> transactionUI.start();
                case "2" -> budgetUI.start();
                case "3" -> goalUI.start();
                case "4" -> reportUI.start();
                case "5" -> profileUI.start();
                case "6" -> notificationUI.displayNotifications();
                case "7" -> exportUI.displayExportOptions();
                case "8" -> refreshData();
                case "0" -> running = false;
                default  -> System.out.println("Invalid choice.");
            }
        }
        System.out.println("Logged out. Goodbye, " + currentUser.getFullName() + "!");
    }

    // ─── US#10 seq: refreshData() ─────────────────────────────────────────────
    /**
     * US#10 seq diagram first call: refreshData(): void
     * Triggers the whole dashboard data load sequence.
     */
    public void refreshData() {
        System.out.println("\n[Loading your dashboard...]");

        // seq: generate(startDate, endDate): Report
        LocalDate start = LocalDate.now().withDayOfMonth(1);
        LocalDate end   = LocalDate.now();
        Report report   = new Report(currentUser.getUserID(), start, end);
        report.generate(); // → internally calls getIncomeVsExpense, getCategoryBreakdown

        // seq: loadRecentTransactions(): void
        loadRecentTransactions();

        // seq: checkThreshold(): void → Budget → BudgetAlert → Notification
        checkThreshold();
    }

    // ─── US#10 seq: loadRecentTransactions() ──────────────────────────────────
    /**
     * US#10 seq: loadRecentTransactions() → Transaction.getRecentTransactions()
     */
    public void loadRecentTransactions() {
        List<Transaction> recent =
                Transaction.getRecentTransactions(currentUser.getUserID(), 5);
        // stored for display in displayDashboard()
    }

    // ─── US#10 seq: checkThreshold() ─────────────────────────────────────────
    /**
     * US#10 seq: checkThreshold(): void
     *   → Budget.checkThreshold()
     *   → BudgetAlert.generate(): void
     *   → Notification.create(type, message): Notification
     */
    public void checkThreshold() {
        List<Budget> userBudgets =
                DataManager.getBudgetsByUser(currentUser.getUserID());

        for (Budget b : userBudgets) {
            b.checkThreshold(); // may internally create BudgetAlert + Notification
        }
    }

    // ─── US#10 seq: displayDashboard() ───────────────────────────────────────
    /**
     * US#10 seq: displayDashboard(): void  → dashboardView
     */
    public void displayDashboard() {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║         PERSONAL BUDGET DASHBOARD    ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.println("Welcome, " + currentUser.getFullName() + "!");
        System.out.println("Date: " + LocalDate.now());

        // ── Financial summary ─────────────────────────────────────────────
        List<Transaction> allTrans =
                DataManager.getTransactionsByUser(currentUser.getUserID());

        double totalIncome  = allTrans.stream()
                .filter(t -> t.getType().equals("IN"))
                .mapToDouble(Transaction::getAmount)
                .sum();
        double totalExpense = allTrans.stream()
                .filter(t -> t.getType().equals("EX"))
                .mapToDouble(Transaction::getAmount)
                .sum();
        double balance = totalIncome - totalExpense;

        System.out.printf("%n  Total Balance : $%.2f%n", balance);
        System.out.printf("  Income        : $%.2f%n", totalIncome);
        System.out.printf("  Expenses      : $%.2f%n", totalExpense);

        // ── Recent transactions ───────────────────────────────────────────
        System.out.println("\n  ── Recent Transactions ──");
        List<Transaction> recent =
                Transaction.getRecentTransactions(currentUser.getUserID(), 5);
        if (recent.isEmpty()) {
            System.out.println("  No transactions yet.");
        } else {
            for (Transaction t : recent) {
                System.out.println("  " + t.toSummaryString());
            }
        }

        // ── Budget warnings (seq: showBudgetWarning()) ────────────────────
        showBudgetWarning();

        // ── Goals summary (seq: showGoalsSummary()) ───────────────────────
        showGoalsSummary();

        // ── Unread notification count ─────────────────────────────────────
        long unread = DataManager.getNotificationsByUser(currentUser.getUserID())
                                 .stream()
                                 .filter(n -> !n.isRead())
                                 .count();
        if (unread > 0) {
            System.out.println("\n  🔔 You have " + unread + " unread notification(s). [Select 6]");
        }
    }

    // ─── US#10 seq: showBudgetWarning() ──────────────────────────────────────
    /**
     * US#10 seq: showBudgetWarning(): void
     */
    public void showBudgetWarning() {
        List<Budget> budgets =
                DataManager.getBudgetsByUser(currentUser.getUserID());
        boolean hasWarning = false;
        for (Budget b : budgets) {
            if (b.getSpentAmount() >= b.getLimitAmount() * 0.8) {
                if (!hasWarning) {
                    System.out.println("\n  ── Budget Warnings ──");
                    hasWarning = true;
                }
                System.out.printf("  ⚠ %s: $%.2f / $%.2f (%.0f%%)%n",
                        b.getCategoryName(),
                        b.getSpentAmount(),
                        b.getLimitAmount(),
                        (b.getSpentAmount() / b.getLimitAmount()) * 100);
            }
        }
    }

    // ─── US#10 seq: showGoalsSummary() ───────────────────────────────────────
    /**
     * US#10 seq: showGoalsSummary(): void
     */
    public void showGoalsSummary() {
        List<FinancialGoal> goals =
                DataManager.getGoalsByUser(currentUser.getUserID());
        if (goals.isEmpty()) return;

        System.out.println("\n  ── Goals Summary ──");
        for (FinancialGoal g : goals) {
            double pct = (g.getCurrentAmount() / g.getTargetAmount()) * 100;
            System.out.printf("  %s: $%.2f / $%.2f (%.0f%%)%n",
                    g.getName(),
                    g.getCurrentAmount(),
                    g.getTargetAmount(),
                    pct);
        }
    }
}

package UI;

import Auth.User;
import Budget.Budget;
import Budget.BudgetAlert;
import Goals.FinancialGoal;
import Notifications.Notification;
import Reports.Report;
import Transaction.Transaction;
import data.DataManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class DashboardUI {

    private Scanner  scanner;
    private User     currentUser;

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

        this.transactionUI  = new TransactionUI(scanner, currentUser);
        this.budgetUI       = new BudgetUI(scanner, currentUser);
        this.goalUI         = new GoalUI(scanner, currentUser);
        this.reportUI       = new ReportUI(scanner, currentUser);
        this.profileUI      = new ProfileUI(scanner, currentUser);
        this.notificationUI = new NotificationUI(scanner, currentUser);
        this.exportUI       = new ExportUI(scanner, currentUser);
    }

    public void start() {
        refreshData();
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

    public void refreshData() {
        System.out.println("\n[Loading your dashboard...]");
        LocalDate start = LocalDate.now().withDayOfMonth(1);
        LocalDate end   = LocalDate.now();
        Report report   = new Report(currentUser.getUserID(), start, end);
        report.generate();
        loadRecentTransactions();
        checkThreshold();
    }

    public void loadRecentTransactions() {
        // Loaded from DataManager on demand in displayDashboard
    }

    public void checkThreshold() {
        List<Budget> userBudgets = DataManager.getBudgetsByUser(currentUser.getUserID());
        for (Budget b : userBudgets) b.checkThreshold();
    }

    public void displayDashboard() {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║         PERSONAL BUDGET DASHBOARD    ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.println("Welcome, " + currentUser.getFullName() + "!");
        System.out.println("Date: " + LocalDate.now());

        List<Transaction> allTrans = DataManager.getTransactionsByUser(currentUser.getUserID());

        double totalIncome  = allTrans.stream()
                .filter(t -> t.getTypeEnum() == Transaction.TransactionType.INCOME)
                .mapToDouble(Transaction::getAmount).sum();
        double totalExpense = allTrans.stream()
                .filter(t -> t.getTypeEnum() == Transaction.TransactionType.EXPENSE)
                .mapToDouble(Transaction::getAmount).sum();
        double balance = totalIncome - totalExpense;

        System.out.printf("%n  Total Balance : $%.2f%n", balance);
        System.out.printf("  Income        : $%.2f%n", totalIncome);
        System.out.printf("  Expenses      : $%.2f%n", totalExpense);

        System.out.println("\n  ── Recent Transactions ──");
        List<Transaction> recent = DataManager.getTransactionsByUser(currentUser.getUserID());
        int start = Math.max(0, recent.size() - 5);
        List<Transaction> last5 = recent.subList(start, recent.size());
        if (last5.isEmpty()) {
            System.out.println("  No transactions yet.");
        } else {
            for (Transaction t : last5) System.out.println("  " + t.toSummaryString());
        }

        showBudgetWarning();
        showGoalsSummary();

        long unread = DataManager.getNotificationsByUser(currentUser.getUserID())
                                 .stream().filter(n -> !n.isRead()).count();
        if (unread > 0)
            System.out.println("\n  You have " + unread + " unread notification(s). [Select 6]");
    }

    public void showBudgetWarning() {
        List<Budget> buds = DataManager.getBudgetsByUser(currentUser.getUserID());
        boolean hasWarning = false;
        for (Budget b : buds) {
            if (b.getLimitAmount() != null && b.getSpentAmount() != null
                    && b.getSpentAmount().doubleValue() >= b.getLimitAmount().doubleValue() * 0.8) {
                if (!hasWarning) { System.out.println("\n  ── Budget Warnings ──"); hasWarning = true; }
                System.out.printf("  WARN %s: $%.2f / $%.2f%n",
                        b.getCategoryName(),
                        b.getSpentAmount().doubleValue(),
                        b.getLimitAmount().doubleValue());
            }
        }
    }

    public void showGoalsSummary() {
        List<FinancialGoal> goalList = DataManager.getGoalsByUser(currentUser.getUserID());
        if (goalList.isEmpty()) return;
        System.out.println("\n  ── Goals Summary ──");
        for (FinancialGoal g : goalList) {
            if (g.getTargetAmount() == null) continue;
            double pct = (g.getCurrentAmount().doubleValue() / g.getTargetAmount().doubleValue()) * 100;
            System.out.printf("  %s: $%.2f / $%.2f (%.0f%%)%n",
                    g.getName(), g.getCurrentAmount().doubleValue(),
                    g.getTargetAmount().doubleValue(), pct);
        }
    }
}

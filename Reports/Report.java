package Reports;

import data.DataManager;
import Transaction.Transaction;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Report implements Serializable {

    private int       userID;
    private LocalDate periodStart;
    private LocalDate periodEnd;

    public Report() {}

    public Report(int userID, LocalDate periodStart, LocalDate periodEnd) {
        this.userID       = userID;
        this.periodStart  = periodStart;
        this.periodEnd    = periodEnd;
    }

    public void generate() {
        System.out.println("[Report] Generating report for user " + userID
                + " from " + periodStart + " to " + periodEnd);
        getIncomeVsExpense();
        getCategoryBreakdown();
    }

    public Map<String, Double> getCategoryBreakdown() {
        Map<String, Double> breakdown = new HashMap<>();
        List<Transaction> txns = DataManager.getTransactionsByUser(userID);
        for (Transaction t : txns) {
            breakdown.merge(t.getCategory(), t.getAmount(), Double::sum);
        }
        return breakdown;
    }

    public void getIncomeVsExpense() {
        List<Transaction> txns = DataManager.getTransactionsByUser(userID);
        double income  = txns.stream()
                .filter(t -> t.getTypeEnum() == Transaction.TransactionType.INCOME)
                .mapToDouble(Transaction::getAmount).sum();
        double expense = txns.stream()
                .filter(t -> t.getTypeEnum() == Transaction.TransactionType.EXPENSE)
                .mapToDouble(Transaction::getAmount).sum();
        System.out.printf("[Report] Income: $%.2f  |  Expenses: $%.2f%n", income, expense);
    }

    public String formatData(String format, List<Transaction> transactions,
                              boolean inclTransactions, boolean inclBudgets, boolean inclGoals) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Financial Report ===\n");
        sb.append("Period: ").append(periodStart).append(" to ").append(periodEnd).append("\n\n");
        if (inclTransactions) {
            sb.append("--- Transactions ---\n");
            for (Transaction t : transactions) sb.append(t).append("\n");
        }
        return sb.toString();
    }

    public LocalDate getPeriodStart() { return periodStart; }
    public LocalDate getPeriodEnd()   { return periodEnd; }
    public int       getUserID()      { return userID; }
}
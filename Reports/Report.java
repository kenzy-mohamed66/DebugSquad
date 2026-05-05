package Reports;

import data.DataManager;
import Transaction.Transaction;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a generated financial report for a specific period.
 *
 * <p>Aggregates data to show income vs expense and category breakdown.
 *
 * @author DebugSquad
 * @version 1.0
 */
public class Report implements Serializable {

    private int       userID;
    private LocalDate periodStart;
    private LocalDate periodEnd;

    /** Constructs an empty {@code Report}. */
    public Report() {}

    /**
     * Constructs a new {@code Report} for a given user and period.
     *
     * @param userID      the ID of the user
     * @param periodStart the start date of the reporting period
     * @param periodEnd   the end date of the reporting period
     */
    public Report(int userID, LocalDate periodStart, LocalDate periodEnd) {
        this.userID       = userID;
        this.periodStart  = periodStart;
        this.periodEnd    = periodEnd;
    }

    /** Generates the report and prints it to the console. */
    public void generate() {
        System.out.println("[Report] Generating report for user " + userID
                + " from " + periodStart + " to " + periodEnd);
        getIncomeVsExpense();
        getCategoryBreakdown();
    }

    /**
     * Aggregates expenses and incomes by category.
     *
     * @return a map linking category names to total spent/earned amounts
     */
    public Map<String, Double> getCategoryBreakdown() {
        Map<String, Double> breakdown = new HashMap<>();
        List<Transaction> txns = DataManager.getTransactionsByUser(userID);
        for (Transaction t : txns) {
            breakdown.merge(t.getCategory(), t.getAmount(), Double::sum);
        }
        return breakdown;
    }

    /**
     * Prints the total income versus total expenses for the user.
     */
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

    /**
     * Formats the provided data into a summary string.
     *
     * @param format           the format type (e.g., CSV, text)
     * @param transactions     the list of transactions to include
     * @param inclTransactions whether to include transaction details
     * @param inclBudgets      whether to include budget details
     * @param inclGoals        whether to include goal details
     * @return the formatted report string
     */
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

    /** @return the start date of the period */
    public LocalDate getPeriodStart() { return periodStart; }
    /** @return the end date of the period */
    public LocalDate getPeriodEnd()   { return periodEnd; }
    /** @return the ID of the user */
    public int       getUserID()      { return userID; }
}
package models;
import data.DataManager;
import java.util.*;

public class Report {
    public Map<String, Double> generate() {
        if (DataManager.allTransactions.isEmpty()) return null;
        return getCategoryBreakdown();
    }

    public Map<String, Double> getCategoryBreakdown() {
        Map<String, Double> breakdown = new HashMap<>();
        for (Transaction t : DataManager.allTransactions) {
            breakdown.put(t.category, breakdown.getOrDefault(t.category, 0.0) + t.amount);
        }
        return breakdown;
    }

    public void getIncomeVsExpense() {
        // Logic to compare total IN vs total EX
    }
}
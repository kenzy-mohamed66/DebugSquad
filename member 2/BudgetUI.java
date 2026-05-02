package UI;

import Models.Budget;
import Models.BudgetAlert;
import Models.Category;
import java.math.BigDecimal;

public class BudgetUI {
    
    // US #4: Display budgets screen
    public void displayBudgets() {
        System.out.println("\n=========================================");
        System.out.println("           MY BUDGETS");
        System.out.println("=========================================");
        System.out.println("Showing all active budgets for current month");
    }
    
    // US #4: Create a new budget (from sequence diagram)
    public void createBudget(Category category, BigDecimal amount, String period, int alertThreshold) {
        System.out.println("\n--- CREATE BUDGET ---");
        
        if (category == null) {
            System.out.println("Error: Category cannot be null");
            return;
        }
        
        Budget budget = new Budget();
        budget.setCategory(category);
        budget.setAmount(amount);
        budget.setAlertThreshold(alertThreshold);
        budget.create();
    }
    
    // US #4 & US #5: Check threshold (from sequence diagram)
    public void checkThreshold(Budget budget) {
        if (budget == null) {
            System.out.println("No budget to check");
            return;
        }
        budget.checkThreshold();
    }
    
    // US #4 & US #5: Show alert (from sequence diagram)
    public void showAlert(String categoryName, BigDecimal spentAmount, BigDecimal budgetAmount) {
        if (budgetAmount == null || budgetAmount.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }
        
        BigDecimal percentage = spentAmount
            .multiply(new BigDecimal(100))
            .divide(budgetAmount, 2, BigDecimal.ROUND_HALF_UP);
        
        System.out.println("\n BUDGET ALERT ");
        
        if (percentage.compareTo(new BigDecimal(100)) >= 0) {
            BigDecimal exceeded = spentAmount.subtract(budgetAmount);
            System.out.println("   Category: " + categoryName);
            System.out.println("   STATUS: EXCEEDED!");
            System.out.println("   You've exceeded your $" + budgetAmount + " budget by $" + exceeded);
        } else if (percentage.compareTo(new BigDecimal(80)) >= 0) {
            System.out.println("   Category: " + categoryName);
            System.out.println("   STATUS: NEAR LIMIT!");
            System.out.println("   You've used " + percentage + "% of your " + budgetAmount + " budget");
        }
    }
    
    // US #4 & US #5: Show progress bar (from sequence diagram)
    public void showProgressBar(BigDecimal spentAmount, BigDecimal totalAmount) {
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) == 0) {
            System.out.println("Progress: 0% [░░░░░░░░░░]");
            return;
        }
        
        int percentage = spentAmount
            .multiply(new BigDecimal(100))
            .divide(totalAmount, 0, BigDecimal.ROUND_HALF_UP)
            .intValue();
        
        if (percentage > 100) percentage = 100;
        
        int filledBars = percentage / 10;
        StringBuilder bar = new StringBuilder("[");
        
        for (int i = 0; i < 10; i++) {
            if (i < filledBars) {
                if (percentage >= 100) {
                    bar.append("█");
                } else if (percentage >= 80) {
                    bar.append("⚠️");
                } else {
                    bar.append("█");
                }
            } else {
                bar.append("░");
            }
        }
        bar.append("]");
        
        String colorIcon = percentage >= 100 ? "🔴" : (percentage >= 80 ? "🟠" : "🟢");
        System.out.println(colorIcon + " Progress: " + percentage + "% " + bar.toString());
        System.out.println("   Spent: $" + spentAmount + " of $" + totalAmount);
    }
    
    // US #4: Update existing budget (from sequence diagram)
    public void update(Budget budget, BigDecimal newAmount, int newAlertThreshold) {
        if (budget == null) {
            System.out.println("No budget to update");
            return;
        }
        
        budget.setAmount(newAmount);
        budget.setAlertThreshold(newAlertThreshold);
        budget.update();
    }
}
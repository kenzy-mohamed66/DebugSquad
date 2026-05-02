package UI;

import Models.FinancialGoal;
import java.math.BigDecimal;
import java.util.Date;

public class GoalUI {
    
    // US #6: Display goals screen (from sequence diagram)
    public void displayGoals() {
        System.out.println("\n=========================================");
        System.out.println("          FINANCIAL GOALS");
        System.out.println("=========================================");
        System.out.println("Showing all active financial goals");
    }
    
    // US #6: Create goal (from sequence diagram)
    public void createGoal(String name, BigDecimal targetAmount, Date deadline, BigDecimal initialSaved) {
        System.out.println("\n--- CREATE GOAL ---");
        
        if (name == null || name.trim().isEmpty()) {
            System.out.println(" Error: Goal name cannot be empty");
            return;
        }
        
        if (targetAmount == null || targetAmount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Error: Target amount must be greater than 0");
            return;
        }
        
        if (deadline != null && deadline.before(new Date())) {
            System.out.println("Error: Deadline must be a future date");
            return;
        }
        
        FinancialGoal goal = new FinancialGoal();
        goal.setName(name);
        goal.setTargetAmount(targetAmount);
        goal.setDeadline(deadline);
        goal.setCurrentAmount(initialSaved != null ? initialSaved : BigDecimal.ZERO);
        goal.create();
    }
    
    // US #6: Add contribution (from sequence diagram)
    public void addContribution(FinancialGoal goal, BigDecimal amount) {
        if (goal == null) {
            System.out.println("Error: Goal not found");
            return;
        }
        
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Error: Contribution amount must be positive");
            return;
        }
        
        goal.addContribution(amount);
    }
    
    // US #6: Calculate remaining (from sequence diagram)
    public BigDecimal calcRemaining(FinancialGoal goal) {
        if (goal == null) {
            System.out.println(" Error: Goal not found");
            return BigDecimal.ZERO;
        }
        
        BigDecimal remaining = goal.calcRemaining();
        System.out.println(" Remaining to save: $" + remaining);
        return remaining;
    }
    
    // US #6: Update progress (from sequence diagram)
    public void updateProgress(FinancialGoal goal) {
        if (goal == null) {
            System.out.println("Error: Goal not found");
            return;
        }
        
        if (goal.getTargetAmount() == null || goal.getTargetAmount().compareTo(BigDecimal.ZERO) == 0) {
            System.out.println("Cannot update progress: Target amount not set");
            return;
        }
        
        BigDecimal percentage = goal.getCurrentAmount()
            .multiply(new BigDecimal(100))
            .divide(goal.getTargetAmount(), 2, BigDecimal.ROUND_HALF_UP);
        
        int filledBars = percentage.divide(new BigDecimal(10), 0, BigDecimal.ROUND_HALF_UP).intValue();
        StringBuilder bar = new StringBuilder("[");
        
        for (int i = 0; i < 10; i++) {
            if (i < filledBars) {
                bar.append("█");
            } else {
                bar.append("░");
            }
        }
        bar.append("]");
        
        System.out.println("\n📈 GOAL PROGRESS: " + goal.getName());
        System.out.println("   " + percentage + "% " + bar.toString());
        System.out.println("   Saved: $" + goal.getCurrentAmount() + " of $" + goal.getTargetAmount());
        
        if (goal.getStatus().equals("COMPLETED")) {
            System.out.println("    STATUS: COMPLETED! ");
        } else {
            System.out.println("   STATUS: IN PROGRESS");
        }
    }
}
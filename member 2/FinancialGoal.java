package Models;

import java.math.BigDecimal;
import java.util.Date;

public class FinancialGoal {
    private int goalId;
    private String name;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private Date deadline;
    private String status; // "IN_PROGRESS", "COMPLETED"
    private BigDecimal monthlySaving;
    
    // Constructor
    public FinancialGoal() {
        this.currentAmount = BigDecimal.ZERO;
        this.status = "IN_PROGRESS";
    }
    
    // US #6: Create financial goal
    public void create() {
        System.out.println("\nFINANCIAL GOAL CREATED ");
        System.out.println("   Name: " + this.name);
        System.out.println("   Target Amount: $" + this.targetAmount);
        System.out.println("   Deadline: " + this.deadline);
        System.out.println("   Initial Saved: $" + this.currentAmount);
        System.out.println("   Monthly Savings Needed: $" + this.monthlySaving);
        this.status = "IN_PROGRESS";
    }
    
    // US #6: Add contribution to goal
    public void addContribution(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Contribution amount must be positive!");
            return;
        }
        
        BigDecimal newAmount = this.currentAmount.add(amount);
        
        if (newAmount.compareTo(this.targetAmount) > 0) {
            this.currentAmount = this.targetAmount;
            System.out.println(" Contribution exceeds target! Setting to target amount.");
        } else {
            this.currentAmount = newAmount;
        }
        
        System.out.println("\n CONTRIBUTION ADDED: $" + amount);
        System.out.println("   Goal: " + this.name);
        System.out.println("   Total Saved: $" + this.currentAmount + " / $" + this.targetAmount);
        
        // Check if goal is completed
        if (this.currentAmount.compareTo(this.targetAmount) >= 0) {
            this.status = "COMPLETED";
            System.out.println(" GOAL COMPLETED! Congratulations!");
        }
    }
    
    // US #6: Calculate remaining amount needed
    public BigDecimal calcRemaining() {
        BigDecimal remaining = this.targetAmount.subtract(this.currentAmount);
        if (remaining.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }
        return remaining;
    }
    
    // Getters and Setters
    public int getGoalId() { return goalId; }
    public void setGoalId(int goalId) { this.goalId = goalId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public BigDecimal getTargetAmount() { return targetAmount; }
    public void setTargetAmount(BigDecimal targetAmount) { this.targetAmount = targetAmount; }
    
    public BigDecimal getCurrentAmount() { return currentAmount; }
    public void setCurrentAmount(BigDecimal currentAmount) { this.currentAmount = currentAmount; }
    
    public Date getDeadline() { return deadline; }
    public void setDeadline(Date deadline) { this.deadline = deadline; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public BigDecimal getMonthlySaving() { return monthlySaving; }
    public void setMonthlySaving(BigDecimal monthlySaving) { this.monthlySaving = monthlySaving; }
}
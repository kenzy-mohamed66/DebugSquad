package Models;
import java.math.BigDecimal;
import java.util.Date;



public class Budget {
    private int budgetId;
    private BigDecimal amount;
    private Date startDate;
    private Date endDate;
    private int alertThreshold;
    private BigDecimal percentAmount;
    private String status; // "ON_TRACK", "NEAR_LIMIT", "EXCEEDED"
    private Category category;
    
    // Constructor
    public Budget() {
        this.percentAmount = BigDecimal.ZERO;
        this.status = "ON_TRACK";
    }
    
    // US #4: Create budget
    public void create() {
        System.out.println("Budget created successfully");
        System.out.println("   Category: " + (category != null ? category.getName() : "None"));
        System.out.println("   Amount: $" + amount);
        System.out.println("   Period: " + startDate + " to " + endDate);
        System.out.println("   Alert at: " + alertThreshold + "%");
        this.status = "ON_TRACK";
        this.percentAmount = BigDecimal.ZERO;
    }
    
    // US #4: Update budget
    public void update() {
        System.out.println("Budget updated successfully");
        System.out.println("   Category: " + (category != null ? category.getName() : "None"));
        System.out.println("   New Amount: $" + amount);
        System.out.println("   New Alert Threshold: " + alertThreshold + "%");
    }
    
    // US #4 & US #5: Calculate remaining budget amount
    public BigDecimal calcRemaining() {
        BigDecimal remaining = this.amount.subtract(this.percentAmount);
        if (remaining.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }
        return remaining;
    }
    
    // US #4 & US #5: Check if spending exceeds threshold
    public void checkThreshold() {
        if (this.amount == null || this.amount.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }
        
        BigDecimal percentage = this.percentAmount
            .multiply(new BigDecimal(100))
            .divide(this.amount, 2, BigDecimal.ROUND_HALF_UP);
        
        System.out.println("Budget Usage: " + percentage + "% of $" + amount);
        
        if (percentage.compareTo(new BigDecimal(100)) >= 0) {
            this.status = "EXCEEDED";
            System.out.println(" STATUS: EXCEEDED - You have overspent!");
        } else if (percentage.compareTo(new BigDecimal(this.alertThreshold)) >= 0) {
            this.status = "NEAR_LIMIT";
            System.out.println(" STATUS: NEAR LIMIT - You are close to your budget limit!");
        } else {
            this.status = "ON_TRACK";
            System.out.println(" STATUS: ON TRACK - You are within budget.");
        }
    }
    
    // Getters and Setters
    public int getBudgetId() { return budgetId; }
    public void setBudgetId(int budgetId) { this.budgetId = budgetId; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    
    public int getAlertThreshold() { return alertThreshold; }
    public void setAlertThreshold(int alertThreshold) { this.alertThreshold = alertThreshold; }
    
    public BigDecimal getPercentAmount() { return percentAmount; }
    public void setPercentAmount(BigDecimal percentAmount) { 
        this.percentAmount = percentAmount;
        checkThreshold();
    }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}

    


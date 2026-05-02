package Models;

import java.util.Date;

public class BudgetAlert {
    private int alertId;
    private int budgetId;
    private String type; // "NEAR_LIMIT", "EXCEEDED"
    private String message;
    private Date timestamp;
    private boolean isRead;
    
    // Constructor
    public BudgetAlert() {}
    
    public BudgetAlert(int budgetId, String type, String message) {
        this.budgetId = budgetId;
        this.type = type;
        this.message = message;
        this.timestamp = new Date();
        this.isRead = false;
    }
    
    // US #5: Generate alert (called by System in sequence diagram)
    public void generate(String type, String message) {
        this.type = type;
        this.message = message;
        this.timestamp = new Date();
        this.isRead = false;
        
        System.out.println("\n🚨 BUDGET ALERT GENERATED 🚨");
        System.out.println("   Type: " + type);
        System.out.println("   Message: " + message);
        System.out.println("   Time: " + timestamp);
    }
    
    // US #5: Create a new alert (from sequence diagram)
    public BudgetAlert create(String type, String message) {
        BudgetAlert alert = new BudgetAlert();
        alert.type = type;
        alert.message = message;
        alert.timestamp = new Date();
        alert.isRead = false;
        System.out.println(" Alert created: [" + type + "] " + message);
        return alert;
    }
    
    // US #5: Mark alert as read
    public void markAsRead() {
        this.isRead = true;
        System.out.println("✓ Alert marked as read: " + this.message);
    }
    
    // Getters and Setters
    public int getAlertId() { return alertId; }
    public void setAlertId(int alertId) { this.alertId = alertId; }
    
    public int getBudgetId() { return budgetId; }
    public void setBudgetId(int budgetId) { this.budgetId = budgetId; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
    
    public boolean isRead() { return isRead; }
    public void setRead(boolean isRead) { this.isRead = isRead; }
}
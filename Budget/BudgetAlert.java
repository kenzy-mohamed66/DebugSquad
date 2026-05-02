package Budget;

import Notifications.Notification;
import data.DataManager;

import java.io.Serializable;
import java.util.Date;

/**
 * BudgetAlert — US#5 & US#10 sequence diagrams.
 *
 * US#5 seq: Budget.checkThreshold() → BudgetAlert.generate() → Notification.create(type, message)
 * US#10 seq: checkThreshold(): void → Budget → BudgetAlert.generate(): void
 *                                   → Notification.create(type:String, message:String): Notification
 */
public class BudgetAlert implements Serializable {

    private int     alertId;
    private int     budgetId;
    private int     userID;          // needed to create Notification
    private String  type;            // "NEAR_LIMIT", "EXCEEDED"
    private String  message;
    private Date    timestamp;
    private boolean isRead;

    public BudgetAlert() {}

    public BudgetAlert(int budgetId, int userID, String type, String message) {
        this.budgetId  = budgetId;
        this.userID    = userID;
        this.type      = type;
        this.message   = message;
        this.timestamp = new Date();
        this.isRead    = false;
    }

    // Legacy constructor (no userID)
    public BudgetAlert(int budgetId, String type, String message) {
        this(budgetId, 0, type, message);
    }

    /**
     * US#5 seq: Budget → BudgetAlert.generate(): void
     *            BudgetAlert → Notification.create(type, message): Notification
     * US#10 seq: BudgetAlert.generate(): void → Notification.create(type,message)
     */
    public void generate(String type, String message) {
        this.type      = type;
        this.message   = message;
        this.timestamp = new Date();
        this.isRead    = false;

        System.out.println("\n BUDGET ALERT: [" + type + "] " + message);

        // US#5 & US#10: create(type, message): Notification
        if (userID > 0) {
            Notification notif = new Notification(userID, type, message);
            DataManager.addNotification(notif);
            System.out.println("  [Notification created for user " + userID + "]");
        }
    }

    public BudgetAlert create(String type, String message) {
        BudgetAlert alert = new BudgetAlert(budgetId, userID, type, message);
        System.out.println(" Alert created: [" + type + "] " + message);
        return alert;
    }

    public void send() {
        System.out.println("[BudgetAlert] Sent: [" + type + "] " + message);
    }

    public void markAsRead() {
        this.isRead = true;
        System.out.println("Alert marked as read: " + this.message);
    }

    // ─── Getters & Setters ────────────────────────────────────────────────────
    public int     getAlertId()    { return alertId; }
    public void    setAlertId(int id) { this.alertId = id; }
    public int     getBudgetId()   { return budgetId; }
    public void    setBudgetId(int id) { this.budgetId = id; }
    public int     getUserID()     { return userID; }
    public void    setUserID(int id) { this.userID = id; }
    public String  getType()       { return type; }
    public void    setType(String t) { this.type = t; }
    public String  getMessage()    { return message; }
    public void    setMessage(String m) { this.message = m; }
    public Date    getTimestamp()  { return timestamp; }
    public void    setTimestamp(Date d) { this.timestamp = d; }
    public boolean isRead()        { return isRead; }
    public void    setRead(boolean r) { this.isRead = r; }
}
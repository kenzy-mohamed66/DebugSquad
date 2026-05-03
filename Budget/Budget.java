package Budget;

import Transaction.Category;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

/**
 * Budget — US#3, US#4, US#5, US#10 sequence diagrams.
 *
 * US#3  seq: Transaction.save() → Budget.checkThreshold()
 * US#4  seq: BudgetUI.createBudget() → Budget.create()
 *            BudgetUI.editBudget()   → Budget.update()
 * US#5  seq: Budget.updateSpentAmount(amount) → checkThreshold()
 *                                             → BudgetAlert.generate()
 *                                             → Notification.create()
 * US#10 seq: DashboardUI.checkThreshold() → Budget.checkThreshold()
 *                                         → BudgetAlert.generate(): void
 */
public class Budget implements Serializable {

    private int        budgetID;
    private int        userID;
    private BigDecimal limitAmount;
    private Date       startDate;
    private Date       endDate;
    private int        alertThreshold;
    private BigDecimal spentAmount;
    private String     status;        // "ON_TRACK", "NEAR_LIMIT", "EXCEEDED"
    private String     categoryName;
    private Category   category;

    public Budget() {
        this.spentAmount = BigDecimal.ZERO;
        this.status      = "ON_TRACK";
    }

    // ─── US#4: create() ───────────────────────────────────────────────────────
    public void create() {
        System.out.println("Budget created successfully");
        System.out.println("   Category: " + getCategoryName());
        System.out.println("   Limit: $" + limitAmount);
        System.out.println("   Alert at: " + alertThreshold + "%");
        this.status      = "ON_TRACK";
        this.spentAmount = BigDecimal.ZERO;
        checkThreshold(); // US#4 seq: create() → checkThreshold()
    }

    // ─── US#4: update() ───────────────────────────────────────────────────────
    public void update() {
        System.out.println("Budget updated: Limit=$" + limitAmount
                + "  Threshold=" + alertThreshold + "%");
    }

    // ─── US#5: updateSpentAmount(amount) ──────────────────────────────────────
    /**
     * US#5 seq: Transaction.save() → Budget.updateSpentAmount(amount)
     *                              → Budget.checkThreshold()
     */
    public void updateSpentAmount(BigDecimal amount) {
        if (this.spentAmount == null) this.spentAmount = BigDecimal.ZERO;
        this.spentAmount = this.spentAmount.add(amount);
        checkThreshold(); // triggers alert chain per US#5
    }

    // ─── US#3 & US#5 & US#10: checkThreshold() ────────────────────────────────
    /**
     * US#3  seq: Budget.checkThreshold(): void
     * US#5  seq: Budget.checkThreshold() → BudgetAlert.generate()
     *                                    → Notification.create(type, message)
     * US#10 seq: checkThreshold(): void → Budget → BudgetAlert.generate(): void
     */
    public void checkThreshold() {
        if (limitAmount == null || limitAmount.compareTo(BigDecimal.ZERO) == 0) return;
        if (spentAmount == null) spentAmount = BigDecimal.ZERO;

        String previousStatus = this.status; // remember before computing

        BigDecimal percentage = spentAmount
                .multiply(new BigDecimal(100))
                .divide(limitAmount, 2, RoundingMode.HALF_UP);

        String catName = getCategoryName();
        String newStatus;

        if (percentage.compareTo(new BigDecimal(100)) >= 0) {
            newStatus = "EXCEEDED";
        } else if (percentage.compareTo(new BigDecimal(alertThreshold)) >= 0) {
            newStatus = "NEAR_LIMIT";
        } else {
            newStatus = "ON_TRACK";
        }

        this.status = newStatus;

        System.out.println("  [Budget] " + newStatus + ": " + catName
                + " (" + percentage + "%)");

        // Only generate alert + notification when status CHANGES
        // This prevents duplicate notifications on every refresh/markAsRead
        if (!newStatus.equals(previousStatus) && !newStatus.equals("ON_TRACK")) {
            String msg = newStatus.equals("EXCEEDED")
                    ? "Budget EXCEEDED for " + catName + "! Spent: $" + spentAmount
                    : "Near limit for " + catName + " (" + percentage + "%)";
            BudgetAlert alert = new BudgetAlert(budgetID, userID, newStatus, msg);
            alert.generate(newStatus, msg);
        }
    }

    // ─── US#5: calcRemaining() ────────────────────────────────────────────────
    public BigDecimal calcRemaining() {
        if (limitAmount == null) return BigDecimal.ZERO;
        if (spentAmount == null) return limitAmount;
        BigDecimal r = limitAmount.subtract(spentAmount);
        return r.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : r;
    }

    // ─── Getters & Setters ────────────────────────────────────────────────────
    public int        getBudgetID()        { return budgetID; }
    public void       setBudgetID(int id)  { this.budgetID = id; }
    public int        getUserID()          { return userID; }
    public void       setUserID(int id)    { this.userID = id; }
    public BigDecimal getLimitAmount()     { return limitAmount; }
    public void       setLimitAmount(BigDecimal a) { this.limitAmount = a; }
    public void       setAmount(BigDecimal a)      { this.limitAmount = a; }
    public BigDecimal getAmount()                  { return limitAmount; }
    public BigDecimal getSpentAmount()     { return spentAmount != null ? spentAmount : BigDecimal.ZERO; }
    public void       setSpentAmount(BigDecimal a) { this.spentAmount = a; }
    public Date       getStartDate()       { return startDate; }
    public void       setStartDate(Date d) { this.startDate = d; }
    public Date       getEndDate()         { return endDate; }
    public void       setEndDate(Date d)   { this.endDate = d; }
    public int        getAlertThreshold()      { return alertThreshold; }
    public void       setAlertThreshold(int t) { this.alertThreshold = t; }
    public String     getStatus()          { return status; }
    public void       setStatus(String s)  { this.status = s; }
    public String     getCategoryName() {
        return (category != null && category.getName() != null)
                ? category.getName() : (categoryName != null ? categoryName : "—");
    }
    public void       setCategoryName(String n)    { this.categoryName = n; }
    public Category   getCategory()                { return category; }
    public void       setCategory(Category c)      { this.category = c; }
}

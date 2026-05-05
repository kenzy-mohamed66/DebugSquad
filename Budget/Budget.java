package Budget;

import Transaction.Category;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

/**
 * Represents a budget set by a user for a specific category.
 *
 * <p>Tracks limits, spent amounts, and status (e.g., ON_TRACK, EXCEEDED).
 * Generates alerts when the spent amount crosses defined thresholds.
 *
 * @author DebugSquad
 * @version 1.0
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

    /**
     * Constructs a new {@code Budget} with default starting values.
     */
    public Budget() {
        this.spentAmount = BigDecimal.ZERO;
        this.status      = "ON_TRACK";
    }

    /**
     * Initializes the budget and triggers the initial threshold check.
     */
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

    /**
     * Prints a confirmation message when a budget is updated.
     */
    // ─── US#4: update() ───────────────────────────────────────────────────────
    public void update() {
        System.out.println("Budget updated: Limit=$" + limitAmount
                + "  Threshold=" + alertThreshold + "%");
    }

    /**
     * Adds to the spent amount and triggers a threshold check.
     *
     * @param amount the amount newly spent
     */
    // ─── US#5: updateSpentAmount(amount) ──────────────────────────────────────
    public void updateSpentAmount(BigDecimal amount) {
        if (this.spentAmount == null) this.spentAmount = BigDecimal.ZERO;
        this.spentAmount = this.spentAmount.add(amount);
        checkThreshold(); // triggers alert chain per US#5
    }

    /**
     * Checks if the spent amount has crossed the alert threshold or limit.
     *
     * <p>If a status boundary is crossed (e.g., from ON_TRACK to NEAR_LIMIT),
     * a {@link BudgetAlert} is generated.
     */
    // ─── US#3 & US#5 & US#10: checkThreshold() ────────────────────────────────
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

    /**
     * Calculates the remaining amount in the budget before the limit is hit.
     *
     * @return the remaining amount, or {@code 0} if limit is exceeded
     */
    // ─── US#5: calcRemaining() ────────────────────────────────────────────────
    public BigDecimal calcRemaining() {
        if (limitAmount == null) return BigDecimal.ZERO;
        if (spentAmount == null) return limitAmount;
        BigDecimal r = limitAmount.subtract(spentAmount);
        return r.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : r;
    }

    // ─── Getters & Setters ────────────────────────────────────────────────────
    /** @return the budget ID */
    public int        getBudgetID()        { return budgetID; }
    /** @param id the budget ID */
    public void       setBudgetID(int id)  { this.budgetID = id; }
    /** @return the user ID */
    public int        getUserID()          { return userID; }
    /** @param id the user ID */
    public void       setUserID(int id)    { this.userID = id; }
    /** @return the budget limit amount */
    public BigDecimal getLimitAmount()     { return limitAmount; }
    /** @param a the budget limit amount */
    public void       setLimitAmount(BigDecimal a) { this.limitAmount = a; }
    /** @param a the budget limit amount */
    public void       setAmount(BigDecimal a)      { this.limitAmount = a; }
    /** @return the budget limit amount */
    public BigDecimal getAmount()                  { return limitAmount; }
    /** @return the total spent amount */
    public BigDecimal getSpentAmount()     { return spentAmount != null ? spentAmount : BigDecimal.ZERO; }
    /** @param a the total spent amount */
    public void       setSpentAmount(BigDecimal a) { this.spentAmount = a; }
    /** @return the budget start date */
    public Date       getStartDate()       { return startDate; }
    /** @param d the budget start date */
    public void       setStartDate(Date d) { this.startDate = d; }
    /** @return the budget end date */
    public Date       getEndDate()         { return endDate; }
    /** @param d the budget end date */
    public void       setEndDate(Date d)   { this.endDate = d; }
    /** @return the alert threshold percentage */
    public int        getAlertThreshold()      { return alertThreshold; }
    /** @param t the alert threshold percentage */
    public void       setAlertThreshold(int t) { this.alertThreshold = t; }
    /** @return the current status (ON_TRACK, NEAR_LIMIT, EXCEEDED) */
    public String     getStatus()          { return status; }
    /** @param s the new status */
    public void       setStatus(String s)  { this.status = s; }
    
    /**
     * Gets the category name safely.
     *
     * @return the name of the category or "—" if not set
     */
    public String     getCategoryName() {
        return (category != null && category.getName() != null)
                ? category.getName() : (categoryName != null ? categoryName : "—");
    }
    
    /** @param n the category name string */
    public void       setCategoryName(String n)    { this.categoryName = n; }
    /** @return the associated {@link Category} object */
    public Category   getCategory()                { return category; }
    /** @param c the associated {@link Category} object */
    public void       setCategory(Category c)      { this.category = c; }
}

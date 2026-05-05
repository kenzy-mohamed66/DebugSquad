package Goals;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Represents a financial goal set by a user.
 *
 * <p>Tracks a target amount to save, current contributions, and a deadline.
 *
 * @author DebugSquad
 * @version 1.0
 */
public class FinancialGoal implements Serializable {

    private int        goalID;
    private int        userID;
    private String     name;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private Date       deadline;
    private String     status;
    private BigDecimal monthlySaving;

    /**
     * Constructs a new {@code FinancialGoal} with zero initial contribution.
     */
    public FinancialGoal() {
        this.currentAmount = BigDecimal.ZERO;
        this.status        = "IN_PROGRESS";
    }

    /**
     * Confirms the creation of the goal to the console.
     */
    public void create() {
        System.out.println("\nFINANCIAL GOAL CREATED");
        System.out.println("   Name: " + this.name);
        System.out.println("   Target: $" + this.targetAmount);
        System.out.println("   Deadline: " + this.deadline);
        this.status = "IN_PROGRESS";
    }

    /**
     * Adds money to the goal's current amount and updates status.
     *
     * <p>If the goal's target is reached, the status is changed to "COMPLETED".
     *
     * @param amount the positive amount to contribute
     */
    public void addContribution(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Contribution must be positive!"); return;
        }
        BigDecimal newAmount = this.currentAmount.add(amount);
        this.currentAmount = newAmount.compareTo(this.targetAmount) > 0 ? this.targetAmount : newAmount;
        System.out.println(" CONTRIBUTION ADDED: $" + amount);
        if (this.currentAmount.compareTo(this.targetAmount) >= 0) {
            this.status = "COMPLETED";
            System.out.println(" GOAL COMPLETED!");
        }
    }

    /**
     * Calculates how much more money is needed to reach the target.
     *
     * @return the remaining amount, or {@code 0} if reached
     */
    public BigDecimal calcRemaining() {
        BigDecimal r = this.targetAmount.subtract(this.currentAmount);
        return r.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : r;
    }

    // Getters and Setters
    /** @return the goal ID */
    public int        getGoalID()        { return goalID; }
    /** @param id the new goal ID */
    public void       setGoalID(int id)  { this.goalID = id; }
    /** @return the goal ID */
    public int        getGoalId()        { return goalID; }
    /** @param id the new goal ID */
    public void       setGoalId(int id)  { this.goalID = id; }
    /** @return the owning user ID */
    public int        getUserID()        { return userID; }
    /** @param id the owning user ID */
    public void       setUserID(int id)  { this.userID = id; }
    /** @return the name of the goal */
    public String     getName()          { return name; }
    /** @param n the name of the goal */
    public void       setName(String n)  { this.name = n; }
    /** @return the overall target amount to save */
    public BigDecimal getTargetAmount()  { return targetAmount; }
    /** @param a the overall target amount to save */
    public void       setTargetAmount(BigDecimal a) { this.targetAmount = a; }
    /** @return the amount currently saved */
    public BigDecimal getCurrentAmount() { return currentAmount; }
    /** @param a the amount currently saved */
    public void       setCurrentAmount(BigDecimal a) { this.currentAmount = a; }
    /** @return the target deadline date */
    public Date       getDeadline()      { return deadline; }
    /** @param d the target deadline date */
    public void       setDeadline(Date d){ this.deadline = d; }
    /** @return the current status ("IN_PROGRESS", "COMPLETED") */
    public String     getStatus()        { return status; }
    /** @param s the new status */
    public void       setStatus(String s){ this.status = s; }
    /** @return the monthly saving target (if calculated) */
    public BigDecimal getMonthlySaving() { return monthlySaving; }
    /** @param m the monthly saving target */
    public void       setMonthlySaving(BigDecimal m) { this.monthlySaving = m; }
}
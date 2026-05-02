package Goals;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class FinancialGoal implements Serializable {

    private int        goalID;
    private int        userID;
    private String     name;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private Date       deadline;
    private String     status;
    private BigDecimal monthlySaving;

    public FinancialGoal() {
        this.currentAmount = BigDecimal.ZERO;
        this.status        = "IN_PROGRESS";
    }

    public void create() {
        System.out.println("\nFINANCIAL GOAL CREATED");
        System.out.println("   Name: " + this.name);
        System.out.println("   Target: $" + this.targetAmount);
        System.out.println("   Deadline: " + this.deadline);
        this.status = "IN_PROGRESS";
    }

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

    public BigDecimal calcRemaining() {
        BigDecimal r = this.targetAmount.subtract(this.currentAmount);
        return r.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : r;
    }

    public int        getGoalID()        { return goalID; }
    public void       setGoalID(int id)  { this.goalID = id; }
    public int        getGoalId()        { return goalID; }
    public void       setGoalId(int id)  { this.goalID = id; }
    public int        getUserID()        { return userID; }
    public void       setUserID(int id)  { this.userID = id; }
    public String     getName()          { return name; }
    public void       setName(String n)  { this.name = n; }
    public BigDecimal getTargetAmount()  { return targetAmount; }
    public void       setTargetAmount(BigDecimal a) { this.targetAmount = a; }
    public BigDecimal getCurrentAmount() { return currentAmount; }
    public void       setCurrentAmount(BigDecimal a) { this.currentAmount = a; }
    public Date       getDeadline()      { return deadline; }
    public void       setDeadline(Date d){ this.deadline = d; }
    public String     getStatus()        { return status; }
    public void       setStatus(String s){ this.status = s; }
    public BigDecimal getMonthlySaving() { return monthlySaving; }
    public void       setMonthlySaving(BigDecimal m) { this.monthlySaving = m; }
}
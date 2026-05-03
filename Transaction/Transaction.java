package Transaction;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Transaction implements Serializable {

    public enum TransactionType { INCOME, EXPENSE }

    private static int idCounter = 1;

    private int             transactionID;
    private int             userID;
    private double          amount;
    private LocalDateTime   dateTime;
    private String          description;
    private String          category;
    private TransactionType typeEnum;
    private String          notes;

    public Transaction(int userID, double amount, String category, String description,
                       String notes, TransactionType typeEnum, LocalDateTime dateTime) {
        this.transactionID = idCounter++;
        this.userID        = userID;
        this.amount        = amount;
        this.category      = category;
        this.description   = description;
        this.notes         = notes;
        this.typeEnum      = typeEnum;
        this.dateTime      = dateTime;
    }
    public Transaction(double amount, String category, String description,
                       String notes, TransactionType typeEnum, LocalDateTime dateTime) {
        this(0, amount, category, description, notes, typeEnum, dateTime);
    }

    public boolean save() {
        return amount > 0
                && amount <= 9_999_999.99
                && category != null
                && !category.isBlank();
    }

    public void delete() {
        this.amount = 0;
    }

    public void edit(String category, String description) {
        if (category    != null && !category.isBlank())    this.category    = category;
        if (description != null && !description.isBlank()) this.description = description;
    }

    public void updateSpentAmount(double newAmount) {
        this.amount = newAmount;
    }

    public static List<Transaction> getRecentTransactions(int userID, int count) {
        return new ArrayList<>();
    }

    public String getType() {
        return typeEnum == TransactionType.INCOME ? "IN" : "EX";
    }

    public LocalDateTime getDate() {
        return dateTime;
    }

    public String toSummaryString() {
        String sign = typeEnum == TransactionType.INCOME ? "+" : "-";
        return String.format("[%d] %-7s | %-20s | %s%.2f EGP | %s",
                transactionID, typeEnum.name(), category,
                sign, amount,
                description == null || description.isEmpty() ? "-" : description);
    }

    public int             getTransactionID() { return transactionID; }
    public int             getUserID()        { return userID; }
    public double          getAmount()        { return amount; }
    public String          getCategory()      { return category; }
    public String          getDescription()   { return description; }
    public String          getNotes()         { return notes; }
    public TransactionType getTypeEnum()      { return typeEnum; }
    public LocalDateTime   getDateTime()      { return dateTime; }

    public String getFormattedDate() {
        return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    @Override
    public String toString() {
        String sign = typeEnum == TransactionType.INCOME ? "+" : "-";
        return String.format("[%d] %-7s | %-20s | %s%.2f EGP | %s | %s",
                transactionID, typeEnum.name(), category,
                sign, amount,
                description == null || description.isEmpty() ? "-" : description,
                getFormattedDate());
    }
}
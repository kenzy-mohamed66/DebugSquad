package Transaction;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a financial transaction (income or expense).
 *
 * <p>Stores details such as amount, category, description, and date.
 * Implements {@link Serializable} for data persistence.
 *
 * @author DebugSquad
 * @version 1.0
 */
public class Transaction implements Serializable {

    /** Defines the possible types of a transaction. */
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

    /**
     * Constructs a new {@code Transaction} with a specific user ID.
     *
     * @param userID      the ID of the user who owns this transaction
     * @param amount      the transaction amount
     * @param category    the category name
     * @param description a short description
     * @param notes       additional notes
     * @param typeEnum    the type (INCOME or EXPENSE)
     * @param dateTime    the date and time of the transaction
     */
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

    /**
     * Constructs a new {@code Transaction} without a specific user ID (defaults to 0).
     *
     * @param amount      the transaction amount
     * @param category    the category name
     * @param description a short description
     * @param notes       additional notes
     * @param typeEnum    the type (INCOME or EXPENSE)
     * @param dateTime    the date and time of the transaction
     */
    public Transaction(double amount, String category, String description,
                       String notes, TransactionType typeEnum, LocalDateTime dateTime) {
        this(0, amount, category, description, notes, typeEnum, dateTime);
    }

    /**
     * Validates this transaction for saving.
     *
     * @return {@code true} if the amount is valid and category is provided; {@code false} otherwise
     */
    public boolean save() {
        return amount > 0
                && amount <= 9_999_999.99
                && category != null
                && !category.isBlank();
    }

    /**
     * Soft deletes this transaction by setting its amount to zero.
     */
    public void delete() {
        this.amount = 0;
    }

    /**
     * Edits the category and description of this transaction.
     *
     * @param category    the new category (ignored if blank)
     * @param description the new description (ignored if blank)
     */
    public void edit(String category, String description) {
        if (category    != null && !category.isBlank())    this.category    = category;
        if (description != null && !description.isBlank()) this.description = description;
    }

    /**
     * Updates the transaction amount.
     *
     * @param newAmount the new transaction amount
     */
    public void updateSpentAmount(double newAmount) {
        this.amount = newAmount;
    }

    /**
     * Retrieves a list of recent transactions for a user.
     *
     * @param userID the ID of the user
     * @param count  the maximum number of transactions to retrieve
     * @return a list of recent transactions (currently returns empty list)
     */
    public static List<Transaction> getRecentTransactions(int userID, int count) {
        return new ArrayList<>();
    }

    /**
     * Returns a short string representing the transaction type.
     *
     * @return {@code "IN"} for income, {@code "EX"} for expense
     */
    public String getType() {
        return typeEnum == TransactionType.INCOME ? "IN" : "EX";
    }

    /**
     * Returns the date and time of this transaction.
     *
     * @return the transaction date
     */
    public LocalDateTime getDate() {
        return dateTime;
    }

    /**
     * Returns a summarized string representation of this transaction.
     *
     * @return a summary string containing ID, type, category, amount, and description
     */
    public String toSummaryString() {
        String sign = typeEnum == TransactionType.INCOME ? "+" : "-";
        return String.format("[%d] %-7s | %-20s | %s%.2f EGP | %s",
                transactionID, typeEnum.name(), category,
                sign, amount,
                description == null || description.isEmpty() ? "-" : description);
    }

    /** @return the unique transaction ID */
    public int             getTransactionID() { return transactionID; }
    /** @return the associated user ID */
    public int             getUserID()        { return userID; }
    /** @return the transaction amount */
    public double          getAmount()        { return amount; }
    /** @return the transaction category */
    public String          getCategory()      { return category; }
    /** @return the transaction description */
    public String          getDescription()   { return description; }
    /** @return additional notes */
    public String          getNotes()         { return notes; }
    /** @return the transaction type enum */
    public TransactionType getTypeEnum()      { return typeEnum; }
    /** @return the full date and time */
    public LocalDateTime   getDateTime()      { return dateTime; }

    /**
     * Formats the transaction date into a readable string.
     *
     * @return the formatted date string (dd/MM/yyyy HH:mm)
     */
    public String getFormattedDate() {
        return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    /**
     * Returns a fully detailed string representation of this transaction.
     *
     * @return a string containing all major transaction details
     */
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
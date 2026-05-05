package UI;

import Auth.User;
import Reports.Report;
import Transaction.Transaction;
import data.DataManager;

import java.io.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Handles the user interface for exporting data to files.
 *
 * <p>Supports CSV and plain TXT output for a specified date range.
 *
 * @author DebugSquad
 * @version 1.0
 */
public class ExportUI {

    private final Scanner scanner;
    private final User    currentUser;

    /**
     * Constructs a new {@code ExportUI}.
     *
     * @param scanner     the scanner for console input
     * @param currentUser the currently logged-in user
     */
    public ExportUI(Scanner scanner, User currentUser) {
        this.scanner     = scanner;
        this.currentUser = currentUser;
    }

    /**
     * Displays the export menu and prompts the user for format and date range.
     */
    public void displayExportOptions() {
        System.out.println("\n-----------------------------------");
        System.out.println("        EXPORT DATA           ");
        System.out.println("-----------------------------------");
        System.out.println("Format:");
        System.out.println("  1. CSV");
        System.out.println("  2. TXT (plain text report)");
        System.out.print("Choose format: ");

        String formatChoice = scanner.nextLine().trim();
        String format;
        switch (formatChoice) {
            case "1" -> format = "CSV";
            case "2" -> format = "TXT";
            default  -> { System.out.println("Invalid choice."); return; }
        }

        System.out.print("Start date (YYYY-MM-DD): ");
        String startStr = scanner.nextLine().trim();
        System.out.print("End date   (YYYY-MM-DD): ");
        String endStr   = scanner.nextLine().trim();

        LocalDate startDate, endDate;
        try {
            startDate = LocalDate.parse(startStr);
            endDate   = LocalDate.parse(endStr);
        } catch (Exception e) {
            System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            return;
        }

        boolean inclTransactions = true ;
        boolean inclBudgets = true ;
        boolean inclGoals = true ;

        generateFile(format, startDate, endDate, inclTransactions, inclBudgets, inclGoals);
    }

    /**
     * Filters transactions and generates the chosen export file.
     *
     * @param format           the requested format ("CSV" or "TXT")
     * @param startDate        the start of the date range
     * @param endDate          the end of the date range
     * @param inclTransactions {@code true} to include transaction list
     * @param inclBudgets      {@code true} to include budgets (unused in current implementation)
     * @param inclGoals        {@code true} to include goals (unused in current implementation)
     */
    public void generateFile(String format,
                             LocalDate startDate, LocalDate endDate,
                             boolean inclTransactions,
                             boolean inclBudgets,
                             boolean inclGoals) {

        List<Transaction> allTransactions = DataManager.getTransactionsByUser(currentUser.getUserID());

        List<Transaction> filtered = allTransactions.stream()
                .filter(t -> {
                    LocalDate d = t.getDate().toLocalDate();
                    return !d.isBefore(startDate) && !d.isAfter(endDate);
                })
                .toList();

        if (filtered.isEmpty()) {
            System.out.println("No transactions found for this date range.");
            return;
        }

        Report report = new Report(currentUser.getUserID(), startDate, endDate);
        report.generate();

        String fileContent = report.formatData(format, filtered, inclTransactions, inclBudgets, inclGoals);

        String fileName = "export_" + currentUser.getUserID()
                        + "_" + startDate + "_to_" + endDate + "." + format.toLowerCase();

        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
            pw.print(fileContent);
            showSuccess();
            downloadFile(fileName);
        } catch (IOException e) {
            System.out.println("Error writing export file: " + e.getMessage());
        }
    }

    /** Displays a generic success message upon successful export. */
    public void showSuccess() {
        System.out.println("Your file has been generated successfully.");
    }

    /**
     * Confirms the generated file name and location to the user.
     *
     * @param fileName the name of the exported file
     */
    public void downloadFile(String fileName) {
        System.out.println("File saved as: " + fileName);
        System.out.println("(Check the project root folder)");
    }
}

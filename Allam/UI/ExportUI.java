package UI;

import models.User;
import models.Report;
import models.Transaction;
import data.DataManager;

import java.io.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * ExportUI — «boundary» class for US#12 Export Financial Data. 
 *
 * Sequence diagram interactions implemented:
 *   displayExportOptions()
 *   generateFile()
 *     → Transaction.getTransactions()
 *     → Report.generate()
 *     → Report.getCategoryBreakdown()
 *     → Report.getIncomeVsExpense()
 *     → Report.formatData(format)
 *   showSuccess()
 *   downloadFile()
 */
public class ExportUI {

    private Scanner scanner;
    private User    currentUser;

    public ExportUI(Scanner scanner, User currentUser) {
        this.scanner     = scanner;
        this.currentUser = currentUser;
    }

    // ─── Main entry point ─────────────────────────────────────────────────────
    /**
     * US#12 seq: displayExportOptions()
     */
    public void displayExportOptions() {
        System.out.println("\n╔══════════════════════════════╗");
        System.out.println("║        EXPORT DATA           ║");
        System.out.println("╚══════════════════════════════╝");
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
        String endStr = scanner.nextLine().trim();

        LocalDate startDate, endDate;
        try {
            startDate = LocalDate.parse(startStr);
            endDate   = LocalDate.parse(endStr);
        } catch (Exception e) {
            System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            return;
        }

        System.out.println("\nInclude (y/n):");
        System.out.print("  Transactions? ");
        boolean inclTransactions = scanner.nextLine().trim().equalsIgnoreCase("y");
        System.out.print("  Budget Summary? ");
        boolean inclBudgets      = scanner.nextLine().trim().equalsIgnoreCase("y");
        System.out.print("  Goals? ");
        boolean inclGoals        = scanner.nextLine().trim().equalsIgnoreCase("y");

        generateFile(format, startDate, endDate, inclTransactions, inclBudgets, inclGoals);
    }

    // ─── US#12: generateFile ──────────────────────────────────────────────────
    /**
     * US#12 seq: generateFile()
     *   → getTransactions() → transactionsList
     *   → generate(): Report
     *   → getCategoryBreakdown(): Map
     *   → getIncomeVsExpense(): Data
     *   → formatData(format:String): File
     */
    public void generateFile(String format,
                             LocalDate startDate, LocalDate endDate,
                             boolean inclTransactions,
                             boolean inclBudgets,
                             boolean inclGoals) {

        // Step 1: get transactions (seq diagram: getTransactions())
        List<Transaction> allTransactions =
                DataManager.getTransactionsByUser(currentUser.getUserID());

        // Filter by date range
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

        // Step 2: generate report (seq diagram: generate(): Report)
        Report report = new Report(currentUser.getUserID(), startDate, endDate);
        report.generate();

        // Step 3: formatData (seq diagram: formatData(format:String): File)
        String fileContent = report.formatData(format, filtered, inclTransactions, inclBudgets, inclGoals);

        // Step 4: write file to disk
        String fileName = "export_" + currentUser.getUserID()
                        + "_" + startDate + "_to_" + endDate + "." + format.toLowerCase();

        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
            pw.print(fileContent);
            showSuccess();          // seq diagram: showSuccess()
            downloadFile(fileName); // seq diagram: downloadFile()
        } catch (IOException e) {
            System.out.println("Error writing export file: " + e.getMessage());
        }
    }

    // ─── US#12: showSuccess ───────────────────────────────────────────────────
    /**
     * US#12 seq: showSuccess(): void
     */
    public void showSuccess() {
        System.out.println("Your file has been generated successfully.");
    }

    // ─── US#12: downloadFile ──────────────────────────────────────────────────
    /**
     * US#12 seq: downloadFile(): void
     * In terminal, we tell the user where the file was saved.
     */
    public void downloadFile(String fileName) {
        System.out.println("File saved as: " + fileName);
        System.out.println("(Check the project root folder)");
    }
}

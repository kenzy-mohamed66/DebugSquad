package UI;

import Auth.User;
import Reports.Report;
import data.DataManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ReportUI {

    private final Scanner scanner;
    private final User    currentUser;

    public ReportUI(Scanner scanner, User currentUser) {
        this.scanner     = scanner;
        this.currentUser = currentUser;
    }

    // ─── start() — main loop ─────────────────────────────────────────────────
    public void start() {
        boolean running = true;
        while (running) {
            System.out.println("\n-----------------------------------");
            System.out.println("         REPORTS            ");
            System.out.println("-----------------------------------");
            System.out.println("1. View Report (select date range)");
            System.out.println("0. Back");
            System.out.print("Choice: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> selectDateRange(); // US#7 seq: selectDateRange()
                case "0" -> running = false;
                default  -> System.out.println("Invalid choice.");
            }
        }
    }

    // ─── US#7 seq: selectDateRange() ─────────────────────────────────────────
    public void selectDateRange() {
        System.out.print("\nStart date (YYYY-MM-DD) [leave blank = start of month]: ");
        String startStr = scanner.nextLine().trim();
        System.out.print("End date   (YYYY-MM-DD) [leave blank = today]: ");
        String endStr = scanner.nextLine().trim();

        LocalDate startDate = startStr.isBlank()
                ? LocalDate.now().withDayOfMonth(1)
                : LocalDate.parse(startStr);
        LocalDate endDate = endStr.isBlank()
                ? LocalDate.now()
                : LocalDate.parse(endStr);

        System.out.println("\n--- Generating Report ---");

        // US#7 seq: Report.generate() → Transaction.fetchTransactions(filter)
        Report report = new Report(currentUser.getUserID(), startDate, endDate);
        report.generate(); // internally fetches transactions

        // Check for empty data
        Map<String, Double> data = report.getCategoryBreakdown();

        if (data == null || data.isEmpty()) {
            // US#7 seq: [Exceptional: No Data Found] → showEmpty()
            showEmpty();
        } else {
            // US#7 seq: [Normal] getCategoryBreakdown(): Map
            // US#7 seq: getIncomeVsExpense(): Data (called inside generate())

            // US#7 seq: displayPieChart()
            displayPieChart(data);

            // US#7 seq: displayBarChart()
            displayBarChart(data);

            // US#7 seq: showInsight("Spending is up 10% in Food")
            showInsight("Tip: Review your top spending category to find savings.");
        }
    }

    // ─── US#7 seq: showEmpty() ────────────────────────────────────────────────
    public void showEmpty() {
        System.out.println("  No data found for this range.");
    }

    // ─── US#7 seq: displayPieChart(data) ─────────────────────────────────────
    public void displayPieChart(Map<String, Double> data) {
        System.out.println("\n  ── Category Breakdown (Pie Chart) ──");
        double total = data.values().stream().mapToDouble(Double::doubleValue).sum();
        data.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .forEach(e -> {
                    int pct = total > 0 ? (int) ((e.getValue() / total) * 100) : 0;
                    int bars = pct / 5;
                    StringBuilder bar = new StringBuilder("  ");
                    for (int i = 0; i < bars; i++) bar.append("█");
                    System.out.printf("  %-18s $%8.2f  (%2d%%) %s%n",
                            e.getKey(), e.getValue(), pct, bar);
                });
    }

    // ─── US#7 seq: displayBarChart() ─────────────────────────────────────────
    public void displayBarChart(Map<String, Double> data) {
        System.out.println("\n  ── Category Breakdown (Bar Chart) ──");
        double max = data.values().stream().mapToDouble(Double::doubleValue).max().orElse(1);
        data.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .forEach(e -> {
                    int bars = (int) ((e.getValue() / max) * 20);
                    StringBuilder bar = new StringBuilder("[");
                    for (int i = 0; i < 20; i++) bar.append(i < bars ? "■" : "·");
                    bar.append("]");
                    System.out.printf("  %-18s %s $%.2f%n",
                            e.getKey(), bar, e.getValue());
                });
    }

    // ─── US#7 seq: showInsight(msg) ──────────────────────────────────────────
    public void showInsight(String msg) {
        System.out.println("\n  Insight: " + msg);
    }
}
package UI;

import Auth.User;
import Reports.Report;

import java.time.LocalDate;
import java.util.ArrayList;
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

    // Main loop
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
                case "1" -> selectDateRange();
                case "0" -> running = false;
                default  -> System.out.println("Invalid choice.");
            }
        }
    }

    public void selectDateRange() {
        System.out.print("\nStart date (YYYY-MM-DD) [leave blank = start of month]: ");
        String startStr = scanner.nextLine().trim();
        System.out.print("End date   (YYYY-MM-DD) [leave blank = today]: ");
        String endStr = scanner.nextLine().trim();

        LocalDate startDate = startStr.isBlank() ? LocalDate.now().withDayOfMonth(1) : LocalDate.parse(startStr);
        LocalDate endDate = endStr.isBlank() ? LocalDate.now() : LocalDate.parse(endStr);

        System.out.println("\n--- Generating Report ---");

        Report report = new Report(currentUser.getUserID(), startDate, endDate);
        report.generate(); // internally fetches transactions

        // Check for empty data
        Map<String, Double> data = report.getCategoryBreakdown();

        // Exceptional Scenario
        if (data == null || data.isEmpty()) {
            showEmpty();
        } 

        // Normal Scenario
        else {
            displayPieChart(data);
            displayBarChart(data);
            showInsight("Review your top spending category to find savings.");
        }
    }

    public void showEmpty() {
        System.out.println("No data found for this range.");
    }

    public void displayPieChart(Map<String, Double> data) {
        System.out.println("\n  Category Breakdown:");

        double total = 0;
        for (double v : data.values()) { 
            total += v; 
        }

        List<Map.Entry<String, Double>> entries = new ArrayList<>(data.entrySet());

        entries.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        for (Map.Entry<String, Double> e : entries) {
            int pct = total > 0 ? (int) ((e.getValue() / total) * 100) : 0;
            int bars = pct / 5;
            StringBuilder bar = new StringBuilder();

            for (int i = 0; i < bars; i++) {
                bar.append("#");
            }

            System.out.printf("  %-18s $%8.2f  (%2d%%) %s%n", e.getKey(), e.getValue(), pct, bar);
        }
    }

    public void displayBarChart(Map<String, Double> data) {
        System.out.println("\n  Spending by Category:");
        
        double max = 0;
        for (double v : data.values()) {
            if (v > max) max = v;
        }

        if (max == 0) max = 1;

        List<Map.Entry<String, Double>> entries = new ArrayList<>(data.entrySet());
        entries.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        for (Map.Entry<String, Double> e : entries) {
            int bars = (int) ((e.getValue() / max) * 20);
            StringBuilder bar = new StringBuilder("[");

            for (int i = 0; i < 20; i++) {
                bar.append(i < bars ? "#" : "-");
            }

            bar.append("]");

            System.out.printf("  %-18s %s $%.2f%n",
                    e.getKey(), bar, e.getValue());
        }
    }

    public void showInsight(String msg) {
        System.out.println("\n  Insight: " + msg);
    }
}
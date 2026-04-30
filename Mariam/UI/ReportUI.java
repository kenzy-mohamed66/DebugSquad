package ui;
import models.Report;
import java.util.Map;

public class ReportUI {
    public void selectDateRange() {
        System.out.println("\n--- Generating Report ---");
        Report report = new Report();
        Map<String, Double> data = report.generate();

        if (data == null) {
            showEmpty();
        } else {
            displayPieChart(data);
            showInsight("Spending is up 10% in Food");
        }
    }

    public void showEmpty() { System.out.println("No data found for this range."); }

    public void displayPieChart(Map<String, Double> data) {
        data.forEach((k, v) -> System.out.println(k + " [####] $" + v));
    }

    public void showInsight(String msg) { System.out.println("Insight: " + msg); }
}
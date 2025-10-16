package UI_Components;

import Helper_Classes.ResourceEffortData;
import javax.swing.*;
import java.awt.*;

public class ResourceEffortBreakdown extends JPanel {
    Table effortTable;
    JTextArea summaryArea;
    JPanel summaryPanel;

    public ResourceEffortBreakdown() {
        super.setLayout(new BorderLayout(5, 5));

        // Create title
        JLabel titleLabel = new JLabel("Resource Effort Breakdown", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        // Create table with columns
        String[] columns = {"Resource Name", "Total Hours", "Total Days", "Tasks Count", "Utilization"};
        effortTable = new Table(columns, false);
        effortTable.setPreferredSize(new Dimension(420, 150));

        // Create summary area
        summaryArea = new JTextArea(4, 30);
        summaryArea.setEditable(false);
        summaryArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        summaryArea.setBorder(BorderFactory.createTitledBorder("Summary Statistics"));
        summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.add(summaryArea, BorderLayout.CENTER);
        summaryPanel.setPreferredSize(new Dimension(420, 100));

        // Add components
        this.add(titleLabel, BorderLayout.NORTH);
        this.add(effortTable, BorderLayout.CENTER);
        this.add(summaryPanel, BorderLayout.SOUTH);
    }

    /**
     * Display resource effort breakdown with detailed information
     */
    public void displayEffortBreakdown(ResourceEffortData[] effortData) {
        // Clear existing data
        effortTable.clearTable();

        if(effortData == null || effortData.length == 0) {
            String[] emptyRow = {"No data", "0.00", "0.00", "0", "N/A"};
            effortTable.insertRow(emptyRow);
            summaryArea.setText("No resource data available.");
            return;
        }

        double totalHours = 0;
        int totalTasks = 0;
        String busiestResource = "";
        double maxHours = 0;

        // Populate table
        for(ResourceEffortData data : effortData) {
            if(data != null) {
                String[] row = new String[5];
                row[0] = data.resourceName;
                row[1] = String.format("%.2f", data.totalHours);
                row[2] = String.format("%.2f", data.totalDays);
                row[3] = String.valueOf(data.taskCount);
                row[4] = data.utilizationLevel;

                effortTable.insertRow(row);

                // Calculate statistics
                totalHours += data.totalHours;
                totalTasks += data.taskCount;

                if(data.totalHours > maxHours) {
                    maxHours = data.totalHours;
                    busiestResource = data.resourceName;
                }
            }
        }

        // Display summary
        StringBuilder summary = new StringBuilder();
        summary.append(String.format("Total Resources: %d\n", effortData.length));
        summary.append(String.format("Total Effort: %.2f hours (%.2f days)\n",
                totalHours, totalHours / 24.0));
        summary.append(String.format("Average Effort per Resource: %.2f hours\n",
                totalHours / effortData.length));
        summary.append(String.format("Busiest Resource: %s (%.2f hours)",
                busiestResource, maxHours));

        summaryArea.setText(summary.toString());
    }

    /**
     * Clear all data
     */
    public void clearData() {
        effortTable.clearTable();
        summaryArea.setText("");
    }
}
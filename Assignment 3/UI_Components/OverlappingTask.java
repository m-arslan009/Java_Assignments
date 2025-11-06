package UI_Components;

import javax.swing.*;
import java.awt.*;

public class OverlappingTask extends JPanel {
    Table overlappingTaskTable;

    public OverlappingTask() {
        // Set a proper layout for the panel
        super.setLayout(new BorderLayout());

        String[] column = {"S#", "Task ID"};
        overlappingTaskTable = new Table(column, false);

        // Wrap the table in a JScrollPane to make it visible
        JScrollPane scrollPane = new JScrollPane(overlappingTaskTable);
        super.add(scrollPane, BorderLayout.CENTER);
    }

    public void displayOverlappingTasks(boolean flag) {
        super.setVisible(flag);
    }

    public void addTaskToTable(int[] tasks) {
        if(tasks == null || tasks.length == 0) {
            String[] row = {"N/A", "No overlapping tasks found"};
            overlappingTaskTable.insertRow(row);
            return;
        }

        int ind = 1;
        for(int val: tasks) {
            if(val > 0) {  // ← Only add valid task IDs
                String[] row = new String[2];
                row[0] = Integer.toString(ind);
                row[1] = Integer.toString(val);
                overlappingTaskTable.insertRow(row);
                ind++;
            }
        }
    }
}
package UI_Components;

import Helper_Classes.ProjectSchedular;
import Helper_Classes.ResourceEffortData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AnalyzeData extends JFrame {
    private Analyze_Type AnalyzeOptions;
    private ProjectCompletion Project_Completion;
    private ProjectSchedular manager;
    private OverlappingTask taskOverlap;
    private ResourceAndTeam resourceAndTeam;
    private ResourceEffortBreakdown effortBreakdown;
    private JPanel displayPanel;

    // NEW: Delete button
    private JButton deleteButton;

    public AnalyzeData(ProjectSchedular obj) {
        this.setLayout(new BorderLayout(10, 10));

        this.manager = obj;
        this.AnalyzeOptions = new Analyze_Type();
        this.add(AnalyzeOptions, BorderLayout.NORTH);

        // Center display panel
        displayPanel = new JPanel(new BorderLayout());
        this.add(displayPanel, BorderLayout.CENTER);

        // Initialize display sections
        Project_Completion = new ProjectCompletion();
        taskOverlap = new OverlappingTask();
        resourceAndTeam = new ResourceAndTeam();
        effortBreakdown = new ResourceEffortBreakdown();

        Project_Completion.setVisible(false);
        taskOverlap.setVisible(false);
        resourceAndTeam.setVisible(false);
        effortBreakdown.setVisible(false);

        // Add delete button at bottom
        setupDeleteButton();

        this.setSize(600, 600);
        this.setTitle("Analyze Project Data");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    // ========================= DELETE BUTTON SETUP =========================
    private void setupDeleteButton() {
        deleteButton = new JButton("Delete Selected Task");
        deleteButton.setBackground(new Color(220, 80, 80));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedTask();
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(deleteButton);
        this.add(buttonPanel, BorderLayout.SOUTH);
    }

    // ========================= DELETE SELECTED TASK =========================
    private void deleteSelectedTask() {
        if (manager != null && manager.getTableList() != null) {
            int selectedRow = manager.getTableComponentFromTableList().getSelectedRow();

            if (selectedRow != -1) {
                int taskId = Integer.parseInt(
                        manager.getTableComponentFromTableList().getValueAt(selectedRow, 0).toString()
                );

                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to delete Task " + taskId + "?",
                        "Confirm Deletion",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    boolean success = manager.deleteTask(taskId);

                    if (success) {
                        JOptionPane.showMessageDialog(this,
                                "Task " + taskId + " deleted successfully!");
                        manager.refreshDataFromDatabase();
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Failed to delete Task " + taskId,
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please select a task from the table to delete",
                        "No Selection",
                        JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Task list is not available or manager not initialized.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    // ========================= DISPLAY PROJECT COMPLETION =========================
    public void displayProjectCompletion(String Date, String Duration, boolean flag) {
        displayPanel.removeAll();

        if (flag) {
            Project_Completion.setData(Date, Duration);
            displayPanel.add(Project_Completion, BorderLayout.CENTER);
            Project_Completion.setVisible(true);
        }

        hideAllExcept("project");
        displayPanel.revalidate();
        displayPanel.repaint();
    }

    // ========================= DISPLAY OVERLAPPING TASKS =========================
    public void displayOverlappingTasks(int[] tasks, boolean flag) {
        displayPanel.removeAll();
        taskOverlap.overlappingTaskTable.clearTable();

        if (flag) {
            taskOverlap.addTaskToTable(tasks);
            displayPanel.add(taskOverlap, BorderLayout.CENTER);
            taskOverlap.setVisible(true);
        }

        hideAllExcept("overlap");
        displayPanel.revalidate();
        displayPanel.repaint();
    }

    // ========================= DISPLAY TEAM & RESOURCES =========================
    public void displayResourceAndTeam(int[] teamTasks, boolean flag) {
        displayPanel.removeAll();
        resourceAndTeam.clearDisplay();

        if (flag) {
            resourceAndTeam.displayTeamInfo(teamTasks);
            displayPanel.add(resourceAndTeam, BorderLayout.CENTER);
            resourceAndTeam.setVisible(true);
        }

        hideAllExcept("team");
        displayPanel.revalidate();
        displayPanel.repaint();
    }

    // ========================= DISPLAY RESOURCE EFFORT =========================
    public void displayResourceEffort(ResourceEffortData[] effortData, boolean flag) {
        displayPanel.removeAll();
        effortBreakdown.clearData();

        if (flag) {
            effortBreakdown.displayEffortBreakdown(effortData);
            displayPanel.add(effortBreakdown, BorderLayout.CENTER);
            effortBreakdown.setVisible(true);
        }

        hideAllExcept("effort");
        displayPanel.revalidate();
        displayPanel.repaint();
    }

    // ========================= HELPER: HIDE UNUSED PANELS =========================
    private void hideAllExcept(String visible) {
        if (!visible.equals("project")) Project_Completion.setVisible(false);
        if (!visible.equals("overlap")) taskOverlap.setVisible(false);
        if (!visible.equals("team")) resourceAndTeam.setVisible(false);
        if (!visible.equals("effort")) effortBreakdown.setVisible(false);
    }

    // ========================= GETTERS =========================
    public ProjectCompletion getProject_Completion() {
        return Project_Completion;
    }

    public OverlappingTask getTaskOverlap() {
        return taskOverlap;
    }

    public ResourceAndTeam getResourceAndTeam() {
        return resourceAndTeam;
    }

    public ResourceEffortBreakdown getEffortBreakdown() {
        return effortBreakdown;
    }

    public Analyze_Type getAnalyzeOptions() {
        return AnalyzeOptions;
    }
}

package UI_Components;

import Helper_Classes.ProjectSchedular;
import Helper_Classes.ResourceEffortData;

import javax.swing.*;
import java.awt.*;

public class AnalyzeData extends JFrame {
    Analyze_Type AnalyzeOptions;
    ProjectCompletion Project_Completion;
    ProjectSchedular manager;
    OverlappingTask taskOverlap;
    ResourceAndTeam resourceAndTeam;
    ResourceEffortBreakdown effortBreakdown;  // ← Add this
    JPanel displayPanel;

    public AnalyzeData(ProjectSchedular obj) {
        this.setLayout(new BorderLayout(10, 10));

        AnalyzeOptions = new Analyze_Type();
        this.add(AnalyzeOptions, BorderLayout.NORTH);

        // Create a panel to hold the display components
        displayPanel = new JPanel(new BorderLayout());

        Project_Completion = new ProjectCompletion();
        taskOverlap = new OverlappingTask();
        resourceAndTeam = new ResourceAndTeam();
        effortBreakdown = new ResourceEffortBreakdown();  // ← Add this

        this.add(displayPanel, BorderLayout.CENTER);
        this.setSize(500, 550);  // Increased size for better display

        this.manager = obj;

        Project_Completion.setVisible(false);
        taskOverlap.setVisible(false);
        resourceAndTeam.setVisible(false);
        effortBreakdown.setVisible(false);  // ← Add this
    }

    public void setVisibility(boolean flag) {
        this.setVisible(flag);
    }

    public void displayProjectCompletion(String Date, String Duration, boolean flag) {
        displayPanel.removeAll();

        if(flag) {
            Project_Completion.setData(Date, Duration);
            displayPanel.add(Project_Completion, BorderLayout.CENTER);
            Project_Completion.setVisible(true);
        }

        hideAllExcept("project");

        displayPanel.revalidate();
        displayPanel.repaint();
    }

    public void displayOverlappingTasks(int[] tasks, boolean flag) {
        displayPanel.removeAll();

        taskOverlap.overlappingTaskTable.clearTable();

        if(flag) {
            taskOverlap.addTaskToTable(tasks);
            displayPanel.add(taskOverlap, BorderLayout.CENTER);
            taskOverlap.setVisible(true);
        }

        hideAllExcept("overlap");

        displayPanel.revalidate();
        displayPanel.repaint();
    }

    public void displayResourceAndTeam(int[] teamTasks, boolean flag) {
        displayPanel.removeAll();

        resourceAndTeam.clearDisplay();

        if(flag) {
            resourceAndTeam.displayTeamInfo(teamTasks);
            displayPanel.add(resourceAndTeam, BorderLayout.CENTER);
            resourceAndTeam.setVisible(true);
        }

        hideAllExcept("team");

        displayPanel.revalidate();
        displayPanel.repaint();
    }

    // ← Update this method
    public void displayResourceEffort(ResourceEffortData[] effortData, boolean flag) {
        displayPanel.removeAll();

        effortBreakdown.clearData();

        if(flag) {
            effortBreakdown.displayEffortBreakdown(effortData);
            displayPanel.add(effortBreakdown, BorderLayout.CENTER);
            effortBreakdown.setVisible(true);
        }

        hideAllExcept("effort");

        displayPanel.revalidate();
        displayPanel.repaint();
    }

    // ← Add helper method
    private void hideAllExcept(String visible) {
        if(!visible.equals("project")) Project_Completion.setVisible(false);
        if(!visible.equals("overlap")) taskOverlap.setVisible(false);
        if(!visible.equals("team")) resourceAndTeam.setVisible(false);
        if(!visible.equals("effort")) effortBreakdown.setVisible(false);
    }

    public ProjectCompletion getProject_Completion() {
        return Project_Completion;
    }

    public OverlappingTask getTaskOverlap() {
        return taskOverlap;
    }

    public ResourceAndTeam getResourceAndTeam() {
        return resourceAndTeam;
    }

    public ResourceEffortBreakdown getEffortBreakdown() {  // ← Add this
        return effortBreakdown;
    }

    public Analyze_Type getAnalyzeOptions() {
        return AnalyzeOptions;
    }
}
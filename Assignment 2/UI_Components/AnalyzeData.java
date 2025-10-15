package UI_Components;

import Helper_Classes.ProjectSchedular;

import javax.swing.*;
import java.awt.*;

public class AnalyzeData extends JFrame {
    Analyze_Type AnalyzeOptions;
    ProjectCompletion Project_Completion;
    ProjectSchedular manager;
    OverlappingTask taskOverlap;
    public AnalyzeData(ProjectSchedular obj) {
        this.setLayout(new GridLayout(3, 1, 10, 10));
        AnalyzeOptions = new Analyze_Type();
        this.add(AnalyzeOptions);

        Project_Completion = new ProjectCompletion();
        taskOverlap = new OverlappingTask();

        Project_Completion.setPreferredSize(new Dimension(380, 100));
        taskOverlap.setPreferredSize(new Dimension(380, 100));

        this.add(Project_Completion);
        this.add(taskOverlap);
        this.setSize(400, 400);

        this.manager = obj;

        Project_Completion.setVisible(false);
        taskOverlap.setVisible(false);

    }

    public void setVisibility(boolean flag) {
        this.setVisible(flag);
    }

    public void displayProjectCompletion(String Date, String Duration, boolean flag) {
        taskOverlap.setVisible(false);

        Project_Completion.setData(Date, Duration);
        Project_Completion.setVisible(flag);

        revalidate();
        repaint();
    }

    public void displayOverlappingTasks(int[] tasks, boolean flag) {

        taskOverlap.overlappingTaskTable.clearTable();

        Project_Completion.setVisible(false);
        taskOverlap.addTaskToTable(tasks);
        taskOverlap.setVisible(flag);

        revalidate();
        repaint();
    }

    public ProjectCompletion getProject_Completion() {
        return Project_Completion;
    }

    public OverlappingTask getTaskOverlap() {
        return taskOverlap;
    }

    public Analyze_Type getAnalyzeOptions() {
        return AnalyzeOptions;
    }
}

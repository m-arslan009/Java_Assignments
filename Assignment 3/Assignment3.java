import java.awt.*;
import javax.swing.*;

import Helper_Classes.ProjectSchedular;
import Helper_Classes.ResourceEffortData;
import UI_Components.*;

public class Assignment3 {
    static String start = "";
    static String end = "";

    static long days = 0;
    static long hours = 0;
    static long min = 0;

    public static void main(String[] args) {
        Header header = new Header();
        ActionButtons actionButtons = new ActionButtons();
        ProjectSchedular schedular = new ProjectSchedular(header);
        JPanel top = new JPanel(new BorderLayout(0, 8));
        top.add(header, BorderLayout.NORTH);
        top.add(actionButtons, BorderLayout.SOUTH);
        schedular.addComponents(top, BorderLayout.NORTH);
        schedular.addComponents(schedular.getTableList(), BorderLayout.SOUTH);
        schedular.displayFrame();

        Upload obj = new Upload();
        AnalyzeData analyzeData = new AnalyzeData(schedular);

        // ✅ NEW PROJECT BUTTON (clears file + database)
        header.getNew().addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(schedular.getFrame(),
                    "Clear all data and start new project?\nThis will clear both table and database.",
                    "New Project", JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                schedular.getTableList().clearTable();
                schedular.clearDatabase();
                JOptionPane.showMessageDialog(schedular.getFrame(), "New project started!");
            }
        });

        // ✅ SAVE BUTTON (saves to both file + database)
        header.getSave().addActionListener(e -> {
            schedular.saveData(); // saves to file
        });

        // ✅ LOAD BUTTON (load from file or database)
        header.getLoad().addActionListener(e -> {
            Object[] options = {"Load from Files", "Load from Database", "Cancel"};
            int choice = JOptionPane.showOptionDialog(schedular.getFrame(),
                    "Load data from:",
                    "Load Project Data",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);

            if (choice == 0) {
                schedular.getTableList().clearTable();
                schedular.uploadTaskDataInTable();
                schedular.uploadResourceDataInTable();
            } else if (choice == 1) {
                schedular.loadFromDatabase();
                JOptionPane.showMessageDialog(null, "Data successfully loaded from Database!");
            }
        });

        // ✅ UPLOAD TASK FILE
        actionButtons.getUpload_Task().addActionListener(e -> {
            if (obj.OpenDialog() == JFileChooser.APPROVE_OPTION) {
                obj.setSelectedFile();
                schedular.uploadTasksFromFile(obj.getSelectedFilePath());
            }
        });

        // ✅ UPLOAD RESOURCE FILE
        actionButtons.getUpload_Resource().addActionListener(e -> {
            if (obj.OpenDialog() == JFileChooser.APPROVE_OPTION) {
                obj.setSelectedFile();
                schedular.uploadResourcesFromFile(obj.getSelectedFilePath());
            }
        });

        // ✅ ANALYZE PROJECT
        actionButtons.getAnalyze().addActionListener(e -> {
            schedular.calculateProjectCompletion();
            start = schedular.getProjectStartTime();
            end = schedular.getProjectEndTime();

            days = schedular.getTotalDays();
            hours = schedular.getTotalHours();
            min = schedular.getTotalMinutes();

            analyzeData.setVisible(true);
        });

        // ✅ ANALYZE: PROJECT TIMING
        analyzeData.getAnalyzeOptions().getProject_Timing().addActionListener(e -> {
            analyzeData.getAnalyzeOptions().isProjectTimingSelected(analyzeData.getProject_Completion());
            analyzeData.displayProjectCompletion(start + " to " + end,
                    days + " days " + hours + " hours " + min + " min", true);
        });

        // ✅ ANALYZE: OVERLAPPING TASKS
        analyzeData.getAnalyzeOptions().getOverlapping_Tasks().addActionListener(e -> {
            int[] task = schedular.overlapingTasks();
            analyzeData.getAnalyzeOptions().isOverlappingTasksSelected(analyzeData.getTaskOverlap());
            analyzeData.displayOverlappingTasks(task, true);
        });

        // ✅ ANALYZE: RESOURCE & TEAM
        analyzeData.getAnalyzeOptions().getResources_and_team().addActionListener(e -> {
            int[] teamTasks = schedular.getTeamCollaborationTasks();
            analyzeData.getAnalyzeOptions().isResources_and_teamSelected(analyzeData.getResourceAndTeam());
            analyzeData.displayResourceAndTeam(teamTasks, true);
        });

        // ✅ ANALYZE: EFFORT BREAKDOWN
        analyzeData.getAnalyzeOptions().getEffort_Breakdown().addActionListener(e -> {
            ResourceEffortData[] effortData = schedular.getResourceEffortBreakdown();
            analyzeData.displayResourceEffort(effortData, true);
        });

        // ✅ VISUALIZE BUTTON
        actionButtons.getVisualize().addActionListener(e -> {
            schedular.visualization();
        });

        System.out.println("🚀 Project Scheduler (SQL Server Integrated) - Application Started");
        System.out.println("===============================================================");
    }
}

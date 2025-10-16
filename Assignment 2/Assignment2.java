import java.awt.*;
import Helper_Classes.ProjectSchedular;
import Helper_Classes.ResourceEffortData;  // ← Add this import
import UI_Components.*;

import javax.swing.*;

public class Assignment2 {
    static String start="";
    static String end ="";

    static long days = 0 ;
    static long hours = 0;
    static long min = 0;

    public static void main(String[] args)  {
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

        header.getNew().addActionListener(e -> {
            schedular.getTableList().clearTable();
            JOptionPane.showMessageDialog(schedular.getFrame(), "New project started!");
        });

        header.getSave().addActionListener(e -> {
            schedular.saveData();
        });

        header.getLoad().addActionListener(e -> {
            schedular.uploadTaskDataInTable();
            schedular.uploadResourceDataInTable();
        });

        actionButtons.getUpload_Task().addActionListener(e -> {
            if(obj.OpenDialog() == JFileChooser.APPROVE_OPTION) {
                obj.setSelectedFile();
                schedular.uploadTasksFromFile(obj.getSelectedFilePath());
            }
        });

        actionButtons.getUpload_Resource().addActionListener(e -> {
            if(obj.OpenDialog() == JFileChooser.APPROVE_OPTION) {
                obj.setSelectedFile();
                schedular.uploadResourcesFromFile(obj.getSelectedFilePath());
                JOptionPane.showMessageDialog(null, "File successfully uploaded");
            }
        });

        actionButtons.getAnalyze().addActionListener(e -> {
            schedular.calculateProjectCompletion();
            start = schedular.getProjectStartTime();
            end = schedular.getProjectEndTime();

            days = schedular.getTotalDays();
            hours = schedular.getTotalHours();
            min = schedular.getTotalMinutes();

            analyzeData.setVisibility(true);
        });

        analyzeData.getAnalyzeOptions().getProject_Timing().addActionListener(e -> {
            System.out.println("[" + java.time.LocalDateTime.now() + "] User: l233025 - Selected: Project Timing");

            analyzeData.getAnalyzeOptions().isProjectTimingSelected(analyzeData.getProject_Completion());
            analyzeData.displayProjectCompletion(start + " to " + end,
                    days + " days " + hours + " hours " + min + " min", true);
        });

        analyzeData.getAnalyzeOptions().getOverlapping_Tasks().addActionListener(e -> {
            System.out.println("[" + java.time.LocalDateTime.now() + "] User: l233025 - Selected: Overlapping Tasks");

            int[] task = schedular.overlapingTasks();

            System.out.println("Number of overlapping tasks: " + task.length);
            System.out.print("Overlapping task IDs: ");
            for(int id : task) {
                if(id != 0) System.out.print(id + " ");
            }
            System.out.println();

            analyzeData.getAnalyzeOptions().isOverlappingTasksSelected(analyzeData.getTaskOverlap());
            analyzeData.displayOverlappingTasks(task, true);
        });

        analyzeData.getAnalyzeOptions().getResources_and_team().addActionListener(e -> {
            System.out.println("[" + java.time.LocalDateTime.now() + "] User: l233025 - Selected: Resources and Team");

            int[] teamTasks = schedular.getTeamCollaborationTasks();

            System.out.println("Number of team collaboration tasks: " + teamTasks.length);
            System.out.print("Team task IDs: ");
            for(int id : teamTasks) {
                System.out.print(id + " ");
            }
            System.out.println();

            analyzeData.getAnalyzeOptions().isResources_and_teamSelected(analyzeData.getResourceAndTeam());
            analyzeData.displayResourceAndTeam(teamTasks, true);
        });

        analyzeData.getAnalyzeOptions().getEffort_Breakdown().addActionListener(e -> {
            System.out.println("[" + java.time.LocalDateTime.now() + "] User: l233025 - Selected: Effort Breakdown");

            ResourceEffortData[] effortData = schedular.getResourceEffortBreakdown();
            System.out.println("Resource effort breakdown:");
            System.out.println("Total resources: " + effortData.length);
            for(ResourceEffortData data : effortData) {
                if(data != null) {
                    System.out.printf("  %s: %.2f hours (%.2f days) - %d tasks - %s utilization%n",
                            data.resourceName, data.totalHours, data.totalDays,
                            data.taskCount, data.utilizationLevel);
                }
            }

            analyzeData.displayResourceEffort(effortData, true);
        });

        actionButtons.getVisualize().addActionListener(e -> {
            schedular.visualization();
        });
    }
}
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
// helper classes
import Helper_Classes.ProjectSchedular;
import Helper_Classes.ResourceManager;
import Helper_Classes.TaskManager;
import UI_Components.*;

import javax.swing.*;

public class Assignment1 {
    static String start="";
    static String end ="";

    static long days = 0 ;
    static long hours = 0;
    static long min = 0;

    public static void main(String[] args)  {
        Header header = new Header();
        ActionButtons actionButtons = new ActionButtons();
//        ProjectSchedular schedular = new ProjectSchedular(taskManagers, resourceManagers, header);
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
            System.out.println(schedular.getProjectStartTime());
            start = schedular.getProjectStartTime();
            end = schedular.getProjectEndTime();

            days = schedular.getTotalDays();
            hours = schedular.getTotalHours();
            min = schedular.getTotalMinutes();

            analyzeData.displayProjectCompletion(null, null, false);

            analyzeData.setVisibility(true);
        });

        analyzeData.getAnalyzeOptions().getProject_Timing().addActionListener(e -> {
            analyzeData.getAnalyzeOptions().isProjectTimingSelected(analyzeData.getProject_Completion());
            analyzeData.displayProjectCompletion(start + " to " + end,
                    days + " days " + hours + " hours " + min + " min", true);
        });

        analyzeData.getAnalyzeOptions().getOverlapping_Tasks().addActionListener(e -> {
            int[] task = schedular.overlapingTasks();
            System.out.println(task.toString());
            analyzeData.getAnalyzeOptions().isOverlappingTasksSelected(analyzeData.getTaskOverlap());
            analyzeData.displayOverlappingTasks(schedular.overlapingTasks(), true);
            analyzeData.displayProjectCompletion("", "", false);
        });

    }
}

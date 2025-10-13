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
import UI_Components.ActionButtons;
import UI_Components.Header;
import UI_Components.Upload;

import javax.swing.*;

public class Assignment1 {

    public static void main(String[] args)  {
//        ArrayList<TaskManager> taskManagers = new ArrayList<>();
//        HashMap<Integer, TaskManager> taskManagers = new HashMap<>();
//        try {
//            FileReader readerObj = new FileReader("Resources/Task.txt");
//            BufferedReader buffObj = new BufferedReader(readerObj);
//            String line;
//            String delimiter = "[,]";
//            int taskId = -1;
//            while((line = buffObj.readLine()) != null) {
//                String[] lineArray = line.split(delimiter);
//                TaskManager tm = new  TaskManager();
//                for(int i = 0; i < lineArray.length; i++) {
//                    if(i == 0) {
//                        taskId = Integer.parseInt(lineArray[i]);
//                        tm.setId(taskId);
//                    } else if(i == 1) {
//                        tm.setName(lineArray[i]);
//                    } else if(i == 2) {
//                        tm.setStartingDate(lineArray[i]);
//                    } else if(i == 3) {
//                        tm.setEndingDate(lineArray[i]);
//                    } else {
//                        tm.setDependsOn(lineArray[i]);
//                    }
//                }
//
//                taskManagers.put(tm.getId(), tm);
//
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        HashMap<String, ResourceManager> resourceManagers = new HashMap<>();
//
//        try {
//            FileReader ResourcesObj = new FileReader("Resources/Resources.txt");
//            BufferedReader buffObj = new BufferedReader(ResourcesObj);
//            String line;
//            String delimiter = "[,]";
//            while((line =  buffObj.readLine()) != null) {
//                String[] data = line.split(delimiter);
//                ResourceManager obj = new  ResourceManager();
//                String name = "";
//
//                for(int i = 0; i < data.length; i++) {
//                    if(i == 0) {
//                        name = data[i];
//                        obj.setName(name);
//                    } else {
//                        obj.setSourceData(data[i]);
//                    }
//                }
//                resourceManagers.put(name, obj);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

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
    }
}

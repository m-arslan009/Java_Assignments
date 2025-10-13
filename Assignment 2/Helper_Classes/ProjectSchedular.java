package Helper_Classes;
import UI_Components.Header;
import UI_Components.Table;

import java.io.*;
import java.util.*;
import javax.swing.*;

public class ProjectSchedular {
    HashMap<Integer, TaskManager> Tasks;
    HashMap<String, ResourceManager> Resources;
    public JFrame frame;
    public Header header;
    Table tableList;


//    public ProjectSchedular(HashMap<Integer, TaskManager> taskManagers, HashMap<String, ResourceManager> resourceManagers,Header h) {
      public ProjectSchedular(Header h) {
        this.header = h;
        String[] columns = {"Id", "Task", "Start", "End", "Dependencies", "Rsources"};
        tableList = new Table(columns, false);

        this.Resources = new HashMap<>();
        this.Tasks = new HashMap<>();
        frame = new JFrame("Project Schedular");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000,600);
    }

    public void addComponents(JComponent component, Object constraint) {
        frame.add(component,  constraint);
    }

    public void displayFrame() {
        frame.setVisible(true);
    }

    public JFrame getFrame() {
        return frame;
    }

    public void loadData() {

          if(Tasks.size() <= 0 && Resources.size() <= 0) {
              JOptionPane.showMessageDialog(frame, "Table is empty");
          }

        for(Map.Entry<Integer, TaskManager> entry : Tasks.entrySet()) {
            String[] RowData = new String[6];
            TaskManager task = entry.getValue();
            RowData[0] = String.valueOf(task.getId());
            RowData[1] = task.getName();
            RowData[2] = task.getStarting();
            RowData[3] = task.getEnding();
            RowData[4] = task.getDependencies().toString();
            StringBuilder resourceName = new StringBuilder();
            for(ResourceManager rs : Resources.values()) {
                HashMap<Integer, Integer> rsMap = rs.getResource();
                if(rsMap.containsKey(entry.getKey())) {
                    int contribution = rsMap.get(entry.getKey());
                    resourceName.append(rs.name);
                    if(contribution < 100) {
                        resourceName.append("*");
                    }
                    resourceName.append(",");
                }
            }

            RowData[5] = resourceName.toString();
            tableList.insertRow(RowData);
        }
    }

    public Table getTableList() {
        return  tableList;
    }

    public void saveData() {
        try {
            File dir = new File("Resources");
            if (!dir.exists()) dir.mkdir();


            FileWriter fw = new FileWriter("Resources/Task.txt", false);

            for (Map.Entry<Integer, TaskManager> TaskMapEntry : Tasks.entrySet()) {
                TaskManager obj = TaskMapEntry.getValue();
                StringBuilder val = new StringBuilder();

                val.append(obj.id).append(", ")
                        .append(obj.name).append(", ")
                        .append(obj.starting).append(", ")
                        .append(obj.ending);

                if (obj.dependsOn != null && !obj.dependsOn.isEmpty()) {
                    val.append(", ");
                    for (int i = 0; i < obj.dependsOn.size(); i++) {
                        val.append(obj.dependsOn.get(i));
                        if (i < obj.dependsOn.size() - 1) {
                            val.append(", ");
                        }
                    }
                }

                fw.write(val.toString());
                fw.write("\n");
            }
            fw.close();

            FileWriter Rfw = new FileWriter("Resources/Resources.txt", false);

            for (Map.Entry<String, ResourceManager> ResourceMapEntry : Resources.entrySet()) {
                ResourceManager obj = ResourceMapEntry.getValue();
                StringBuilder val = new StringBuilder();

                val.append(obj.name);

                for (Map.Entry<Integer, Integer> contribution : obj.getResource().entrySet()) {
                    val.append(", ").append(contribution.getKey()).append(":").append(contribution.getValue());
                }

                Rfw.write(val.toString());
                Rfw.write("\n");
            }

            Rfw.close();
            JOptionPane.showMessageDialog(frame, "Project saved successfully!");

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error saving data: " + e.getMessage());
        }
    }

    public void uploadTasksFromFile(String f) {
        System.out.println(f);
        try {
            FileReader readerObj = new FileReader(f);
            BufferedReader buffObj = new BufferedReader(readerObj);
            String line;
            String delimiter = "[,]";
            int taskId = -1;
            while((line = buffObj.readLine()) != null) {
                String[] lineArray = line.split(delimiter);
                TaskManager tm = new  TaskManager();
                for(int i = 0; i < lineArray.length; i++) {
                    if(i == 0) {
                        taskId = Integer.parseInt(lineArray[i]);
                        tm.setId(taskId);
                    } else if(i == 1) {
                        tm.setName(lineArray[i]);
                    } else if(i == 2) {
                        tm.setStartingDate(lineArray[i]);
                    } else if(i == 3) {
                        tm.setEndingDate(lineArray[i]);
                    } else {
                        tm.setDependsOn(lineArray[i]);
                    }
                }

                this.Tasks.put(tm.getId(), tm);
            }
            JOptionPane.showMessageDialog(frame, "Now you can Load data into table using \"Load\" Button on TOP LEFT corner", "File successfully uploaded",  JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void uploadResourcesFromFile(String path) {
        System.out.println(path);

        try {
            FileReader ResourcesObj = new FileReader(path);
            BufferedReader buffObj = new BufferedReader(ResourcesObj);
            String line;
            String delimiter = "[,]";
            while((line =  buffObj.readLine()) != null) {
                String[] data = line.split(delimiter);
                ResourceManager obj = new  ResourceManager();
                String name = "";

                for(int i = 0; i < data.length; i++) {
                    if(i == 0) {
                        name = data[i];
                        obj.setName(name);
                    } else {
                        obj.setSourceData(data[i]);
                    }
                }
                this.Resources.put(name, obj);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void uploadTaskDataInTable() {
          if(Tasks.isEmpty()) {
              JOptionPane.showMessageDialog(frame, "Upload tak file");
              return;
          }
        for(Map.Entry<Integer, TaskManager> TaskMapEntry : Tasks.entrySet()) {
            String[] RowData = new String[6];
            TaskManager task = TaskMapEntry.getValue();

            RowData[0] = String.valueOf(task.getId());
            RowData[1] = task.getName();
            RowData[2] = task.getStarting();
            RowData[3] = task.getEnding();
            if(task.getDependencies().size() > 0) {
                RowData[4] = String.join(",", task.getDependencies());
            } else {
                RowData[4] = "";
            }
            RowData[5] = "";
            tableList.insertRow(RowData);
        }
    }

    public void uploadResourceDataInTable() {
          if(Tasks.isEmpty()) {
              JOptionPane.showMessageDialog(frame, "Resources heavily depends on Task data", "Upload Task File", JOptionPane.ERROR_MESSAGE);
                return;
          }

          int row = 0;

        for(Map.Entry<Integer, TaskManager> entry : Tasks.entrySet()) {
            TaskManager task = entry.getValue();
            StringBuilder resourceName = new StringBuilder();
            for(ResourceManager rs : Resources.values()) {
                HashMap<Integer, Integer> rsMap = rs.getResource();
                if(rsMap.containsKey(entry.getKey())) {
                    int contribution = rsMap.get(entry.getKey());
                    resourceName.append(rs.name);
                    if(contribution < 100) {
                        resourceName.append("*");
                    }
                    resourceName.append(",");
                }
            }
            tableList.updateCell( resourceName.toString(), row++,5);
        }
    }

}
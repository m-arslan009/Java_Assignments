import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
// helper classes
import Helper_Classes.ProjectSchedular;
import Helper_Classes.ResourceManager;
import Helper_Classes.TaskManager;

public class Assignment1 {

    public static void main(String[] args)  {
//        ArrayList<TaskManager> taskManagers = new ArrayList<>();
        HashMap<Integer, TaskManager> taskManagers = new HashMap<>();
        int ind = 0;
        try {
            FileReader readerObj = new FileReader("Resources/Task.txt");
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

                taskManagers.put(tm.getId(), tm);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            for(int i = 0; i < taskManagers.size(); i++) {
                taskManagers.get(i).PrintTaskManager();
            }
//            for(TaskManager tm : taskManagers) {
//                tm.PrintTaskManager();
//            }
        }

//        ProjectSchedular schedular = new ProjectSchedular(taskManagers);
//        ProjectSchedular.ProjectCompletion completion = schedular.calculateProjectCompletion();
//        schedular.printProjectCompletion(completion);


//        ArrayList<ResourceManager> resourceManagers = new ArrayList<ResourceManager>();
//
//        try {
//            FileReader ResourcesObj = new FileReader("Resources/Resources.txt");
//            BufferedReader buffObj = new BufferedReader(ResourcesObj);
//            String line;
//            String delimiter = "[,]";
//            while((line =  buffObj.readLine()) != null) {
//                String[] data = line.split(delimiter);
//                ResourceManager obj = new  ResourceManager();
//
//                for(int i = 0; i < data.length; i++) {
//                    if(i == 0) {
//                        obj.setName(data[i]);
//                    } else {
//                        obj.setSourceData(data[i]);
//                    }
//                }
//                resourceManagers.add(obj);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            for(ResourceManager r : resourceManagers) {
//                r.PrintResources();
//            }
//        }
    }
}

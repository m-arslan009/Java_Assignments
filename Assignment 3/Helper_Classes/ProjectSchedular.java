package Helper_Classes;

import Database.ResourceData;
import Database.TaskData;
import UI_Components.Header;
import UI_Components.Rectangle;
import UI_Components.Table;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ProjectSchedular {
    // Primary in-memory stores
    public HashMap<Integer, TaskManager> Tasks;
    public HashMap<String, ResourceManager> Resources;

    // UI components
    public JFrame frame;
    public Header header;
    Table tableList;

    // Analytics fields
    String projectStartTime;
    String projectEndTime;
    long totalDays;
    long totalHours;
    long totalMinutes;
    ArrayList<String> criticalPath;

    // DB helpers
    private Database.TaskData taskData;
    private Database.ResourceData resourceData;

    // utility map (unused in current code but kept for compatibility)
    private Map<String, TaskManager> taskMap;

    public ProjectSchedular(Header h) {
        this.header = h;
        String[] columns = {"Id", "Task", "Start", "End", "Dependencies", "Resources", "Delete"};
        tableList = new Table(columns, false);

        this.Resources = new HashMap<>();
        this.Tasks = new HashMap<>();

        // initialize DB handlers
        this.taskData = new Database.TaskData();
        this.resourceData = new Database.ResourceData();

        // Set up edit and delete callbacks
        setupTableCallbacks();

        frame = new JFrame("Project Schedular");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);
    }

    private void setupTableCallbacks() {
        // Edit callback
        tableList.setOnEditCallback(row -> {
            handleEditTask(row);
        });

        // Delete callback
        tableList.setOnDeleteCallback(row -> {
            handleDeleteTask(row);
        });
    }

    private void handleEditTask(int row) {
        try {
            // Get task ID from the table
            int taskId = Integer.parseInt(tableList.getValueAt(row, 0).toString());
            TaskManager task = Tasks.get(taskId);

            if (task == null) {
                JOptionPane.showMessageDialog(frame, "Task not found!");
                return;
            }

            // Create edit dialog
            JDialog editDialog = new JDialog(frame, "Edit Task", true);
            editDialog.setLayout(new GridLayout(7, 2, 10, 10));
            editDialog.setSize(500, 400);

            // Create input fields
            JTextField nameField = new JTextField(task.getName());
            JTextField startDateField = new JTextField(task.getStartingDate());
            JTextField startTimeField = new JTextField(task.getStartingTime());
            JTextField endDateField = new JTextField(task.getEndingDate());
            JTextField endTimeField = new JTextField(task.getEndingTime());
            JTextField depsField = new JTextField(String.join(",", task.getDependencies()));

            // Add labels and fields
            editDialog.add(new JLabel("Task Name:"));
            editDialog.add(nameField);
            editDialog.add(new JLabel("Start Date (yyyy-MM-dd):"));
            editDialog.add(startDateField);
            editDialog.add(new JLabel("Start Time (HH:mm):"));
            editDialog.add(startTimeField);
            editDialog.add(new JLabel("End Date (yyyy-MM-dd):"));
            editDialog.add(endDateField);
            editDialog.add(new JLabel("End Time (HH:mm):"));
            editDialog.add(endTimeField);
            editDialog.add(new JLabel("Dependencies (comma-separated IDs):"));
            editDialog.add(depsField);

            // Buttons
            JButton saveBtn = new JButton("Save");
            JButton cancelBtn = new JButton("Cancel");

            saveBtn.addActionListener(e -> {
                // Update task object
                task.setName(nameField.getText().trim());
                task.setStartingDate(startDateField.getText().trim() + " " + startTimeField.getText().trim());
                task.setEndingDate(endDateField.getText().trim() + " " + endTimeField.getText().trim());

                // Update dependencies
                task.getDependencies().clear();
                String depsText = depsField.getText().trim();
                if (!depsText.isEmpty()) {
                    for (String dep : depsText.split(",")) {
                        task.setDependsOn(dep.trim());
                    }
                }

                // Recalculate timespan
                task.CalculateTimeSpan();

                // Update in database and files
                taskData.updateTask(task);
                saveData();

                // Refresh table
                refreshTable();

                JOptionPane.showMessageDialog(editDialog, "Task updated successfully!");
                editDialog.dispose();
            });

            cancelBtn.addActionListener(e -> editDialog.dispose());

            editDialog.add(saveBtn);
            editDialog.add(cancelBtn);

            editDialog.setLocationRelativeTo(frame);
            editDialog.setVisible(true);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error editing task: " + ex.getMessage());
        }
    }

    private void handleDeleteTask(int row) {
        try {
            // Get task ID from the table
            int taskId = Integer.parseInt(tableList.getValueAt(row, 0).toString());
            TaskManager task = Tasks.get(taskId);

            if (task == null) {
                JOptionPane.showMessageDialog(frame, "Task not found!");
                return;
            }

            // Confirm deletion
            int confirm = JOptionPane.showConfirmDialog(frame,
                    "Are you sure you want to delete task: " + task.getName() + "?\n" +
                            "This will remove it from both the database and files.",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            // Remove from memory
            Tasks.remove(taskId);

            // Remove from database
            taskData.deleteTask(taskId);

            // Remove task from all resources in memory
            for (ResourceManager resource : Resources.values()) {
                resource.getResource().remove(taskId);
            }

            // Update resources in database - remove task from all resources
            resourceData.removeTaskFromResources(taskId);

            // Save updated data to files
            saveData();

            // Refresh table
            refreshTable();

            JOptionPane.showMessageDialog(frame, "Task deleted successfully from tasks and resources!");

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error deleting task: " + ex.getMessage());
        }
    }

    private void refreshTable() {
        tableList.clearTable();
        uploadTaskDataInTable();
        uploadResourceDataInTable();
    }

    // ========== UI helpers ==========
    public void addComponents(JComponent component, Object constraint) {
        frame.add(component, constraint);
    }

    public void displayFrame() {
        frame.setVisible(true);
    }

    public JFrame getFrame() {
        return frame;
    }

    public Table getTableList() {
        return tableList;
    }

    public JTable getTableComponentFromTableList() {
        return tableList.getTable();
    }

    // ========== Save (files + database) ==========
    public void saveData() {
        try {
            System.out.println("=== Starting Save Process ===");
            System.out.println("Tasks to save: " + Tasks.size());
            System.out.println("Resources to save: " + Resources.size());

            // Debug: Print resource details
            for (Map.Entry<String, ResourceManager> entry : Resources.entrySet()) {
                ResourceManager rm = entry.getValue();
                System.out.println("Resource: " + rm.name + " has " + rm.getResource().size() + " task assignments");
            }

            // --- Save to files (backwards compatible) ---
            File dir = new File("Resources");
            if (!dir.exists()) dir.mkdir();

            // Save tasks
            File taskFile = new File(dir, "Task.txt");
            try (BufferedWriter fw = new BufferedWriter(new FileWriter(taskFile, false))) {
                for (Map.Entry<Integer, TaskManager> entry : Tasks.entrySet()) {
                    TaskManager task = entry.getValue();
                    StringBuilder line = new StringBuilder();

                    line.append(task.id).append(",")
                            .append(task.name.trim()).append(",")
                            .append(task.starting.trim()).append(",")
                            .append(task.ending.trim());

                    if (task.dependsOn != null && !task.dependsOn.isEmpty()) {
                        for (String dep : task.dependsOn) {
                            line.append(",").append(dep.trim());
                        }
                    }

                    fw.write(line.toString());
                    fw.newLine();
                }
            }

            // Save resources
            File resourceFile = new File(dir, "Resources.txt");
            try (BufferedWriter Rfw = new BufferedWriter(new FileWriter(resourceFile, false))) {
                for (Map.Entry<String, ResourceManager> entry : Resources.entrySet()) {
                    ResourceManager resource = entry.getValue();
                    StringBuilder line = new StringBuilder();

                    line.append(resource.name.trim());

                    for (Map.Entry<Integer, Integer> contribution : resource.getResource().entrySet()) {
                        line.append(",").append(contribution.getKey()).append(":").append(contribution.getValue());
                    }

                    Rfw.write(line.toString());
                    Rfw.newLine();
                }
            }

            // --- Save to database ---
            try {

                taskData.saveTasks(Tasks);

                resourceData.saveResources(Resources);

                // Verify save
                boolean hasResourceData = resourceData.hasData();

            } catch (Exception dbEx) {
                dbEx.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Saved to files but error saving to DB:\n" + dbEx.getMessage(),
                        "DB Save Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(frame, "Project saved successfully!\n" +
                    "- Tasks: " + Tasks.size() + "\n" +
                    "- Resources: " + Resources.size() + "\n" +
                    "Check console for detailed logs.");

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("File save error: " + e.getMessage());
            JOptionPane.showMessageDialog(frame, "Error saving data: " + e.getMessage());
        }
    }

    // ========== Load from DB ==========
    public void loadFromDatabase() {
        try {
            System.out.println("=== Starting Load Process from Database ===");

            Tasks = taskData.loadTasks();
            System.out.println("✓ Loaded " + Tasks.size() + " tasks from database");

            Resources = resourceData.loadResources();
            System.out.println("✓ Loaded " + Resources.size() + " resources from database");

            // Debug: Print loaded resources
            for (Map.Entry<String, ResourceManager> entry : Resources.entrySet()) {
                ResourceManager rm = entry.getValue();
                System.out.println("  Resource: " + rm.name + " with " + rm.getResource().size() + " assignments");
            }

            // Ensure timespans are computed
            for (TaskManager t : Tasks.values()) {
                try {
                    t.CalculateTimeSpan();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            // Refresh table
            System.out.println("Refreshing table...");
            refreshTable();
            System.out.println("=== Load Process Complete ===");

            JOptionPane.showMessageDialog(frame, "Loaded from database:\n" +
                    Tasks.size() + " tasks\n" +
                    Resources.size() + " resources\n" +
                    "Check console for detailed logs.");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Load error: " + e.getMessage());
            JOptionPane.showMessageDialog(frame, "Error loading from database: " + e.getMessage());
        }
    }

    // ========== File upload methods ==========
    public void uploadTasksFromFile(String f) {
        System.out.println(f);
        try {
            FileReader readerObj = new FileReader(f);
            BufferedReader buffObj = new BufferedReader(readerObj);
            String line;
            String delimiter = "[,]";
            int taskId = -1;
            while ((line = buffObj.readLine()) != null) {
                String[] lineArray = line.split(delimiter);
                TaskManager tm = new TaskManager();
                for (int i = 0; i < lineArray.length; i++) {
                    if (i == 0) {
                        if(lineArray[i] != "") {
                            taskId = Integer.parseInt(lineArray[i]);
                            tm.setId(taskId);
                        }

                    } else if (i == 1) {
                        tm.setName(lineArray[i]);
                    } else if (i == 2) {
                        tm.setStartingDate(lineArray[i]);
                    } else if (i == 3) {
                        tm.setEndingDate(lineArray[i]);
                    } else {
                        tm.setDependsOn(lineArray[i]);
                    }
                }

                this.Tasks.put(tm.getId(), tm);
            }
            JOptionPane.showMessageDialog(frame, "Now you can Load data into table using \"Load\" Button on TOP LEFT corner", "File successfully uploaded", JOptionPane.INFORMATION_MESSAGE);
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
            while ((line = buffObj.readLine()) != null) {
                String[] data = line.split(delimiter);
                ResourceManager obj = new ResourceManager();
                String name = "";

                for (int i = 0; i < data.length; i++) {
                    if (i == 0) {
                        name = data[i];
                        obj.setName(name);
                    } else {
                        String TaskAssignment = data[i].trim();
                        if(TaskAssignment.contains(":")) {
                            String[] contribution = TaskAssignment.split(":");
                            if(Tasks.containsKey(Integer.parseInt(contribution[0]))) {
                                obj.setSourceData(TaskAssignment);
                            }
                        }
                    }
                }
                this.Resources.put(name, obj);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ========== Table population ==========
    public void uploadTaskDataInTable() {
        if (Tasks.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Upload task file");
            return;
        }
        for (Map.Entry<Integer, TaskManager> TaskMapEntry : Tasks.entrySet()) {
            String[] RowData = new String[6];
            TaskManager task = TaskMapEntry.getValue();

            RowData[0] = String.valueOf(task.getId());
            RowData[1] = task.getName();
            RowData[2] = task.getStarting();
            RowData[3] = task.getEnding();
            if (task.getDependencies().size() > 0) {
                RowData[4] = String.join(",", task.getDependencies());
            } else {
                RowData[4] = "";
            }
            RowData[5] = "";
            tableList.insertRow(RowData);
        }
    }

    public void uploadResourceDataInTable() {
        if (Tasks.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Resources heavily depends on Task data", "Upload Task File", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (Resources.isEmpty()) {
            System.out.println("No resources to load");
            return;
        }

        // Create a map for quick lookup: taskId -> row index in table
        HashMap<Integer, Integer> taskIdToRowMap = new HashMap<>();
        int currentRow = 0;
        for (Integer taskId : Tasks.keySet()) {
            taskIdToRowMap.put(taskId, currentRow);
            currentRow++;
        }

        // For each resource, update the corresponding task rows
        for (Map.Entry<String, ResourceManager> entry : Resources.entrySet()) {
            ResourceManager resource = entry.getValue();
            String resourceName = resource.name;

            // For each task this resource is assigned to
            for (Map.Entry<Integer, Integer> taskEntry : resource.getResource().entrySet()) {
                Integer taskId = taskEntry.getKey();
                Integer contribution = taskEntry.getValue();

                // Find the row for this task
                Integer rowIndex = taskIdToRowMap.get(taskId);

                if (rowIndex != null) {
                    // Get current resource string from table
                    String currentResources = (String) tableList.getValueAt(rowIndex, 5);
                    if (currentResources == null) {
                        currentResources = "";
                    }

                    // Build the resource string
                    StringBuilder resourceStr = new StringBuilder(currentResources);
                    if (resourceStr.length() > 0) {
                        resourceStr.append(",");
                    }
                    resourceStr.append(resourceName);

                    // Add asterisk if contribution is less than 100%
                    if (contribution < 100) {
                        resourceStr.append("*");
                    }

                    // Update the cell
                    tableList.updateCell(resourceStr.toString(), rowIndex, 5);
                } else {
                    System.out.println("Warning: Task ID " + taskId + " not found in table for resource " + resourceName);
                }
            }
        }
    }

    public boolean deleteTask(int id) {
        if (taskData != null) {
            taskData.deleteTask(id);
            Tasks.remove(id);

            // Remove from resources
            for (ResourceManager resource : Resources.values()) {
                resource.getResource().remove(id);
            }
            resourceData.removeTaskFromResources(id);

            return true;
        }
        return false;
    }

    public void refreshDataFromDatabase() {
        if (taskData != null) {
            this.Tasks = taskData.loadTasks();
        }
        if (resourceData != null) {
            this.Resources = resourceData.loadResources();
        }
    }

    public void clearDatabase() {
        TaskData taskData = new TaskData();
        ResourceData resourceData = new ResourceData();
        resourceData.clearResources();
        taskData.clearTasks();
    }

    // ========== Analytical helpers ==========
    public StringBuilder[] TotalDurationOfEachResource() {
        if (Resources.isEmpty()) {
            return new StringBuilder[0];
        }

        StringBuilder[] ResourceDurationArr = new StringBuilder[this.Resources.values().size()];
        int ind = 0;

        for (ResourceManager resource : this.Resources.values()) {
            double totalDuration = 0;
            int taskCount = 0;

            for (Map.Entry<Integer, Integer> entry : resource.getResource().entrySet()) {
                TaskManager task = Tasks.get(entry.getKey());
                Integer allocationPercentage = entry.getValue();

                if (task != null) {
                    double taskDurationHours = task.timeSpan.days * 24.0 +
                            task.timeSpan.Hours +
                            (task.timeSpan.Min / 60.0);

                    double resourceEffort = (allocationPercentage / 100.0) * taskDurationHours;
                    totalDuration += resourceEffort;
                    taskCount++;
                }
            }

            StringBuilder Duration = new StringBuilder();
            Duration.append(resource.name)
                    .append(": ")
                    .append(String.format("%.2f", totalDuration))
                    .append(" hours")
                    .append(" (")
                    .append(taskCount)
                    .append(" tasks)");

            ResourceDurationArr[ind++] = Duration;
        }

        return ResourceDurationArr;
    }

    public void calculateProjectCompletion() {
        if (Tasks.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Upload task file");
            return;
        }

        for (TaskManager task : Tasks.values()) {
            task.CalculateTimeSpan();
        }

        LocalDateTime projectStart = findEarliestStartTime();
        Map<String, LocalDateTime> taskCompletionTimes = calculateTaskCompletionTimes();
        LocalDateTime projectEnd = findLatestCompletionTime(taskCompletionTimes);

        Duration projectDuration = Duration.between(projectStart, projectEnd);
        this.totalDays = projectDuration.toDays();
        this.totalHours = projectDuration.toHours() % 24;
        this.totalMinutes = projectDuration.toMinutes() % 60;

        ArrayList<String> criticalPath = findCriticalPath(taskCompletionTimes, projectEnd);
        this.criticalPath = criticalPath;
    }

    private LocalDateTime findEarliestStartTime() {
        LocalDateTime earliest = null;

        for (TaskManager task : Tasks.values()) {
            LocalDate date = LocalDate.parse(task.getStartingDate());
            LocalTime time = LocalTime.parse(task.getStartingTime());
            LocalDateTime taskStart = LocalDateTime.of(date, time);

            if (earliest == null || taskStart.isBefore(earliest)) {
                earliest = taskStart;
            }
        }
        this.projectStartTime = String.valueOf(earliest);
        return earliest;
    }

    private Map<String, LocalDateTime> calculateTaskCompletionTimes() {
        Map<String, LocalDateTime> completionTimes = new HashMap<>();
        Set<String> processed = new HashSet<>();

        for (TaskManager task : Tasks.values()) {
            calculateTaskCompletionTimeRecursive(task, completionTimes, processed);
        }

        return completionTimes;
    }

    private LocalDateTime calculateTaskCompletionTimeRecursive(TaskManager task,
                                                               Map<String, LocalDateTime> completionTimes,
                                                               Set<String> processed) {
        if (processed.contains(task.getName())) {
            return completionTimes.get(task.getName());
        }

        LocalDateTime taskCanStart = null;

        if (task.getDependencies() != null && !task.getDependencies().isEmpty()) {
            for (String dependency : task.getDependencies()) {
                TaskManager depTask = Tasks.get(Integer.parseInt(dependency.trim()));
                if (depTask != null) {
                    LocalDateTime depCompletion = calculateTaskCompletionTimeRecursive(
                            depTask, completionTimes, processed);

                    if (taskCanStart == null || depCompletion.isAfter(taskCanStart)) {
                        taskCanStart = depCompletion;
                    }
                }
            }
        }

        if (taskCanStart == null) {
            LocalDate startDate = LocalDate.parse(task.getStartingDate());
            LocalTime startTime = LocalTime.parse(task.getStartingTime());
            taskCanStart = LocalDateTime.of(startDate, startTime);
        }

        Duration taskDuration = Duration.ofDays(task.getDays())
                .plusHours(task.getHours())
                .plusMinutes(task.getMin());

        LocalDateTime taskCompletion = taskCanStart.plus(taskDuration);

        completionTimes.put(task.getName(), taskCompletion);
        processed.add(task.getName());

        return taskCompletion;
    }

    private LocalDateTime findLatestCompletionTime(Map<String, LocalDateTime> taskCompletionTimes) {
        LocalDateTime latest = null;

        for (LocalDateTime completion : taskCompletionTimes.values()) {
            if (latest == null || completion.isAfter(latest)) {
                latest = completion;
            }
        }

        this.projectEndTime = String.valueOf(latest);
        return latest;
    }

    private ArrayList<String> findCriticalPath(Map<String, LocalDateTime> taskCompletionTimes,
                                               LocalDateTime projectEnd) {
        ArrayList<String> criticalPath = new ArrayList<>();

        for (Map.Entry<String, LocalDateTime> entry : taskCompletionTimes.entrySet()) {
            if (entry.getValue().equals(projectEnd)) {
                criticalPath.add(entry.getKey());
            }
        }

        return criticalPath;
    }

    public int[] overlapingTasks() {
        return overlapingTasksInternal();
    }

    private int[] overlapingTasksInternal() {
        ArrayList<Integer> overlappedTasksList = new ArrayList<>();

        for (TaskManager Task : this.Tasks.values()) {
            ArrayList<String> dependencies = Task.getDependencies();

            if (dependencies == null || dependencies.isEmpty()) {
                continue;
            }

            for (String dependency : dependencies) {
                TaskManager indTask = null;

                for (TaskManager task : this.Tasks.values()) {
                    if (task.getId() == Integer.parseInt(dependency.trim())) {
                        indTask = task;
                        break;
                    }
                }

                if (indTask != null) {
                    LocalDate taskStart = LocalDate.parse(Task.getStartingDate());
                    LocalDate taskEnd = LocalDate.parse(Task.getEndingDate());

                    LocalDate depStart = LocalDate.parse(indTask.getStartingDate());
                    LocalDate depEnd = LocalDate.parse(indTask.getEndingDate());

                    if (!(taskEnd.isBefore(depStart) || taskStart.isAfter(depEnd))) {
                        if (!overlappedTasksList.contains(Task.getId())) {
                            overlappedTasksList.add(Task.getId());
                        }
                    }
                }
            }
        }

        int[] overlappedTasks = new int[overlappedTasksList.size()];
        for (int i = 0; i < overlappedTasksList.size(); i++) {
            overlappedTasks[i] = overlappedTasksList.get(i);
        }

        return overlappedTasks;
    }

    public int[] compareResources() {
        ArrayList<Integer> teamTasksList = new ArrayList<>();

        ArrayList<ResourceManager> resourceList = new ArrayList<>(this.Resources.values());

        for (int i = 0; i < resourceList.size() - 1; i++) {
            ResourceManager curr = resourceList.get(i);

            for (int j = i + 1; j < resourceList.size(); j++) {
                ResourceManager next = resourceList.get(j);

                for (Integer id : curr.getResource().keySet()) {
                    if (next.getResource().containsKey(id) && !teamTasksList.contains(id)) {
                        teamTasksList.add(id);
                    }
                }
            }
        }

        int[] teamTasks = new int[teamTasksList.size()];
        for (int i = 0; i < teamTasksList.size(); i++) {
            teamTasks[i] = teamTasksList.get(i);
        }

        return teamTasks;
    }

    public long getTotalMinutes() {
        return this.totalMinutes;
    }

    public long getTotalHours() {
        return this.totalHours;
    }

    public long getTotalDays() {
        return this.totalDays;
    }

    public String getProjectEndTime() {
        return this.projectEndTime;
    }

    public String getProjectStartTime() {
        return this.projectStartTime;
    }

    public int[] getTeamCollaborationTasks() {
        ArrayList<Integer> teamTasksList = new ArrayList<>();

        for (Map.Entry<Integer, TaskManager> entry : Tasks.entrySet()) {
            int taskId = entry.getKey();
            int resourceCount = 0;

            for (ResourceManager resource : Resources.values()) {
                if (resource.getResource().containsKey(taskId)) {
                    resourceCount++;
                }
            }

            if (resourceCount >= 2) {
                teamTasksList.add(taskId);
            }
        }

        int[] teamTasks = new int[teamTasksList.size()];
        for (int i = 0; i < teamTasksList.size(); i++) {
            teamTasks[i] = teamTasksList.get(i);
        }

        return teamTasks;
    }

    public ResourceEffortData[] getResourceEffortBreakdown() {
        if (Resources.isEmpty()) {
            return new ResourceEffortData[0];
        }

        ResourceEffortData[] effortDataArray = new ResourceEffortData[Resources.size()];
        int index = 0;

        for (ResourceManager resource : Resources.values()) {
            double totalHours = 0;
            int taskCount = 0;

            for (Map.Entry<Integer, Integer> entry : resource.getResource().entrySet()) {
                TaskManager task = Tasks.get(entry.getKey());
                Integer allocationPercentage = entry.getValue();

                if (task != null) {
                    if (task.timeSpan == null) {
                        task.CalculateTimeSpan();
                    }

                    double taskDurationHours = task.timeSpan.days * 24.0 +
                            task.timeSpan.Hours +
                            (task.timeSpan.Min / 60.0);

                    double resourceEffort = (allocationPercentage / 100.0) * taskDurationHours;
                    totalHours += resourceEffort;
                    taskCount++;
                }
            }

            effortDataArray[index++] = new ResourceEffortData(
                    resource.name,
                    totalHours,
                    taskCount
            );
        }

        return effortDataArray;
    }

    @Deprecated
    public StringBuilder[] DurationOfEachResource() {
        if (Resources.isEmpty()) {
            return new StringBuilder[0];
        }

        StringBuilder[] ResourceDurationArr = new StringBuilder[this.Resources.values().size()];
        int ind = 0;

        for (ResourceManager resource : this.Resources.values()) {
            double totalDuration = 0;
            int taskCount = 0;

            for (Map.Entry<Integer, Integer> entry : resource.getResource().entrySet()) {
                TaskManager task = Tasks.get(entry.getKey());
                Integer allocationPercentage = entry.getValue();

                if (task != null) {
                    if (task.timeSpan == null) {
                        task.CalculateTimeSpan();
                    }

                    double taskDurationHours = task.timeSpan.days * 24.0 +
                            task.timeSpan.Hours +
                            (task.timeSpan.Min / 60.0);

                    double resourceEffort = (allocationPercentage / 100.0) * taskDurationHours;
                    totalDuration += resourceEffort;
                    taskCount++;
                }
            }

            StringBuilder Duration = new StringBuilder();
            Duration.append(resource.name)
                    .append(": ")
                    .append(String.format("%.2f", totalDuration))
                    .append(" hours (")
                    .append(String.format("%.2f", totalDuration / 24.0))
                    .append(" days) - ")
                    .append(taskCount)
                    .append(" tasks");

            ResourceDurationArr[ind++] = Duration;
        }

        return ResourceDurationArr;
    }

    // ========== Visualization (Gantt) ==========
    public void visualization() {
        JFrame chartFrame = new JFrame("Gantt Chart");
        chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(Color.WHITE);

        int scale = 20;
        Random rand = new Random();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        LocalDateTime projectStart = getProjectStart();
        if (projectStart == null) {
            JOptionPane.showMessageDialog(frame, "Cannot visualize: project start not found");
            return;
        }

        container.add(Box.createRigidArea(new Dimension(0, 5)));

        for (TaskManager task : Tasks.values()) {
            try {
                LocalDateTime startDate = LocalDateTime.parse(task.startingDate + " " + task.startingTime, formatter);
                LocalDateTime endDate = LocalDateTime.parse(task.endingDate + " " + task.endingTime, formatter);

                long days = Duration.between(startDate, endDate).toDays();
                if (days <= 0) days = 1;

                int startPixel = (int) Duration.between(projectStart, startDate).toDays() * scale;
                int durationPixel = (int) (days * scale);

                Color bgColor = new Color(rand.nextInt(200), rand.nextInt(200), rand.nextInt(200));
                Color textColor = Color.WHITE;

                JPanel info = new JPanel(new BorderLayout(5, 5));
                JLabel starting = new JLabel(task.startingDate);
                JLabel ending = new JLabel(task.endingDate);
                Rectangle taskRect = new Rectangle(task.name, startPixel, durationPixel, bgColor, textColor);

                info.add(starting, BorderLayout.WEST);
                info.add(taskRect, BorderLayout.CENTER);
                info.add(ending, BorderLayout.EAST);

                container.add(info);
                container.add(Box.createRigidArea(new Dimension(0, 5)));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        chartFrame.add(new JScrollPane(container));
        chartFrame.setSize(1000, 600);
        chartFrame.setLocationRelativeTo(null);
        chartFrame.setVisible(true);
    }

    private LocalDateTime getProjectStart() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime min = null;
        for (TaskManager task : Tasks.values()) {
            try {
                LocalDateTime start = LocalDateTime.parse(task.startingDate + " " + task.startingTime, formatter);
                if (min == null || start.isBefore(min)) {
                    min = start;
                }
            } catch (Exception e) {
                // ignore parse errors per task
            }
        }
        return min;
    }

    private LocalDateTime getProjectEnd() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime max = null;
        for (TaskManager task : Tasks.values()) {
            try {
                LocalDateTime end = LocalDateTime.parse(task.endingDate + " " + task.endingTime, formatter);
                if (max == null || end.isAfter(max)) {
                    max = end;
                }
            } catch (Exception e) {
                // ignore parse errors per task
            }
        }
        return max;
    }
}
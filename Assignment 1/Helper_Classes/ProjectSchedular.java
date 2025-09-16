package Helper_Classes;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ProjectSchedular {
    private ArrayList<TaskManager> tasks;
    private Map<String, TaskManager> taskMap;

    public ProjectSchedular(ArrayList<TaskManager> tasks) {
        this.tasks = tasks;
        this.taskMap = new HashMap<>();

        // Create a map for easy task lookup
        for (TaskManager task : tasks) {
            taskMap.put(task.getName(), task);
        }
    }

    public class ProjectCompletion {
        LocalDateTime projectStartTime;
        LocalDateTime projectEndTime;
        long totalDays;
        long totalHours;
        long totalMinutes;
        ArrayList<String> criticalPath;

        public ProjectCompletion(LocalDateTime start, LocalDateTime end,
                                 long days, long hours, long minutes,
                                 ArrayList<String> criticalPath) {
            this.projectStartTime = start;
            this.projectEndTime = end;
            this.totalDays = days;
            this.totalHours = hours;
            this.totalMinutes = minutes;
            this.criticalPath = criticalPath;
        }
    }

    public ProjectCompletion calculateProjectCompletion() {
        if (tasks.isEmpty()) {
            return null;
        }

        // Calculate each task's duration first
        for (TaskManager task : tasks) {
            task.CalculateTimeSpan();
        }

        // Find project start time (earliest task start)
        LocalDateTime projectStart = findEarliestStartTime();

        // Calculate task completion times considering dependencies
        Map<String, LocalDateTime> taskCompletionTimes = calculateTaskCompletionTimes();

        // Find project end time (latest task completion)
        LocalDateTime projectEnd = findLatestCompletionTime(taskCompletionTimes);

        // Calculate total project duration
        Duration projectDuration = Duration.between(projectStart, projectEnd);
        long days = projectDuration.toDays();
        long hours = projectDuration.toHours() % 24;
        long minutes = projectDuration.toMinutes() % 60;

        // Find critical path
        ArrayList<String> criticalPath = findCriticalPath(taskCompletionTimes, projectEnd);

        return new ProjectCompletion(projectStart, projectEnd, days, hours, minutes, criticalPath);
    }

    private LocalDateTime findEarliestStartTime() {
        LocalDateTime earliest = null;
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");

        for (TaskManager task : tasks) {

            LocalDate date = LocalDate.parse(task.getStartingDate());
            LocalTime time = LocalTime.parse(task.getStartingTime());
            LocalDateTime taskStart = LocalDateTime.of(date, time);

            if (earliest == null || taskStart.isBefore(earliest)) {
                earliest = taskStart;
            }
        }
        return earliest;
    }

    private Map<String, LocalDateTime> calculateTaskCompletionTimes() {
        Map<String, LocalDateTime> completionTimes = new HashMap<>();

        // Process tasks in dependency order
        Set<String> processed = new HashSet<>();

        for (TaskManager task : tasks) {
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

        // Calculate when this task can start (after all dependencies complete)
        LocalDateTime taskCanStart = null;

        // Check dependencies
        if (task.getDependencies() != null && !task.getDependencies().isEmpty()) {
            for (String dependency : task.getDependencies()) {
                TaskManager depTask = taskMap.get(dependency.trim());
                if (depTask != null) {
                    LocalDateTime depCompletion = calculateTaskCompletionTimeRecursive(
                    depTask, completionTimes, processed);

                    if (taskCanStart == null || depCompletion.isAfter(taskCanStart)) {
                        taskCanStart = depCompletion;
                    }
                }
            }
        }

        // If no dependencies or earliest dependency completion, use task's scheduled start
        if (taskCanStart == null) {

            LocalDate startDate = LocalDate.parse(task.getStartingDate());
            LocalTime startTime = LocalTime.parse(task.getStartingTime());
            taskCanStart = LocalDateTime.of(startDate, startTime);
        }

        // Calculate task completion time
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

        return latest;
    }

    private ArrayList<String> findCriticalPath(Map<String, LocalDateTime> taskCompletionTimes,
                                               LocalDateTime projectEnd) {
        ArrayList<String> criticalPath = new ArrayList<>();

        // Find tasks that complete at project end time (critical tasks)
        for (Map.Entry<String, LocalDateTime> entry : taskCompletionTimes.entrySet()) {
            if (entry.getValue().equals(projectEnd)) {
                criticalPath.add(entry.getKey());
            }
        }

        return criticalPath;
    }

    public void overlapingTasks(ArrayList<TaskManager> taskManagers) {
        for(TaskManager taskManager : taskManagers) {
            for(int i = 0; i < taskManager.getDependencies().size(); i++) {

            }
        }
    }

    public void printProjectCompletion(ProjectCompletion completion) {
        if (completion == null) {
            System.out.println("No tasks to calculate project completion.");
            return;
        }

        System.out.println("\n========== PROJECT COMPLETION ANALYSIS ==========");
        System.out.println("Project Start Time: " + completion.projectStartTime);
        System.out.println("Project End Time: " + completion.projectEndTime);
        System.out.println("Total Project Duration: " +
                completion.totalDays + " Days, " +
                completion.totalHours + " Hours, " +
                completion.totalMinutes + " Minutes");

        System.out.println("\nCritical Path Tasks:");
        for (String taskName : completion.criticalPath) {
            System.out.println("- " + taskName);
        }
        System.out.println("==================================================\n");
    }
}
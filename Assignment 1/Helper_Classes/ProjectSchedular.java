package Helper_Classes;

import java.lang.reflect.Array;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ProjectSchedular {
    private HashMap<Integer,TaskManager> tasks;
    private Map<String, TaskManager> taskMap;

    public ProjectSchedular(HashMap<Integer, TaskManager> tasks) {
        this.tasks = tasks;
        this.taskMap = new HashMap<>();

        // Create a map for easy task lookup
        for (TaskManager task : tasks.values()) {
            taskMap.put(task.getName(), task);
        }
    }

    public void TotalDurationOfEachResource(HashMap<String, ResourceManager> resources, HashMap<Integer, TaskManager> tasks) {
        for(ResourceManager resource : resources.values()) {
            double totalDuration = 0;

            for(Map.Entry<Integer, Integer> entry : resource.getResource().entrySet()) {
                TaskManager task = tasks.get(entry.getKey());
                Integer allocationPercentage = entry.getValue();

                if(task != null) {
                    double taskDurationHours = task.timeSpan.days * 24.0 +
                            task.timeSpan.Hours +
                            (task.timeSpan.Min / 60.0);
                    double resourceEffort = (allocationPercentage / 100.0) * taskDurationHours;
                    totalDuration += resourceEffort;
                }
            }

            System.out.println(resource.name + ": " + String.format("%.2f", totalDuration) + " hours");
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

        for (TaskManager task : tasks.values()) {
            task.CalculateTimeSpan();
        }

        LocalDateTime projectStart = findEarliestStartTime();

        Map<String, LocalDateTime> taskCompletionTimes = calculateTaskCompletionTimes();

        LocalDateTime projectEnd = findLatestCompletionTime(taskCompletionTimes);

        Duration projectDuration = Duration.between(projectStart, projectEnd);
        long days = projectDuration.toDays();
        long hours = projectDuration.toHours() % 24;
        long minutes = projectDuration.toMinutes() % 60;

        ArrayList<String> criticalPath = findCriticalPath(taskCompletionTimes, projectEnd);

        return new ProjectCompletion(projectStart, projectEnd, days, hours, minutes, criticalPath);
    }

    private LocalDateTime findEarliestStartTime() {
        LocalDateTime earliest = null;
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");

        for (TaskManager task : tasks.values()) {

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

        Set<String> processed = new HashSet<>();

        for (TaskManager task : tasks.values()) {
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

    public void overlapingTasks(HashMap<Integer,TaskManager> taskManagers) {
        for(TaskManager Task : taskManagers.values()) {
            ArrayList<String> dependencies = Task.getDependencies();
            for(String dependency : dependencies) {
                TaskManager indTask = null;
                for(TaskManager task : taskManagers.values()) {
                    if(task.getId() == Integer.parseInt(dependency)) {
                        indTask = task;
                        break;
                    }
                }

                if(indTask != null) {
                    LocalDate taskStart = LocalDate.parse(Task.getStartingDate());
                    LocalDate taskEnd   = LocalDate.parse(Task.getEndingDate());

                    LocalDate depStart  = LocalDate.parse(indTask.getStartingDate());
                    LocalDate depEnd    = LocalDate.parse(indTask.getEndingDate());

                    if(!(taskEnd.isBefore(depStart) || taskStart.isAfter(depEnd))) {
                        System.out.println("Task: " + Task.getId());
                    }
                }
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
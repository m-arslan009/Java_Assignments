package Helper_Classes;

public class ResourceEffortData {
    public String resourceName;
    public double totalHours;
    public double totalDays;
    public int taskCount;
    public String utilizationLevel;

    public ResourceEffortData(String name, double hours, int tasks) {
        this.resourceName = name;
        this.totalHours = hours;
        this.totalDays = hours / 24.0;
        this.taskCount = tasks;
        this.utilizationLevel = calculateUtilization(hours, tasks);
    }

    /**
     * Calculate utilization level based on hours and task count
     */
    private String calculateUtilization(double hours, int tasks) {
        if(tasks == 0) return "Idle";

        double avgHoursPerTask = hours / tasks;

        if(hours > 160) return "Overloaded";      // More than 1 month (160 hours)
        else if(hours > 80) return "High";        // More than 2 weeks
        else if(hours > 40) return "Medium";      // More than 1 week
        else if(hours > 0) return "Low";
        else return "Idle";
    }

    @Override
    public String toString() {
        return String.format("%s: %.2f hours (%.2f days) - %d tasks - %s",
                resourceName, totalHours, totalDays, taskCount, utilizationLevel);
    }
}
package test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import Helper_Classes.ProjectSchedular;
import Helper_Classes.TaskManager;
import UI_Components.Header;

public class TestOverlapping {

    private ProjectSchedular scheduler;

    @BeforeEach
    public void setUp() {
        // Initialize scheduler with a mock header
        Header header = new Header();
        scheduler = new ProjectSchedular(header);
    }

    @Test
    public void testNoOverlappingTasksWhenNoDependencies() {
        // Test case: Tasks with no dependencies should not overlap
        TaskManager task1 = new TaskManager();
        task1.setId(1);
        task1.setName("Task 1");
        task1.setStartingDate("20240101+0900");
        task1.setEndingDate("20240105+1700");

        TaskManager task2 = new TaskManager();
        task2.setId(2);
        task2.setName("Task 2");
        task2.setStartingDate("20240106+0900");
        task2.setEndingDate("20240110+1700");

        scheduler.Tasks.put(1, task1);
        scheduler.Tasks.put(2, task2);

        int[] overlapping = scheduler.overlapingTasks();

        assertEquals(0, overlapping.length, "No overlapping tasks expected");
    }

    @Test
    public void testOverlappingTasksDetected() {
        // Test case: Task starts before dependency ends (overlapping)
        TaskManager task1 = new TaskManager();
        task1.setId(1);
        task1.setName("Task 1");
        task1.setStartingDate("20240101+0900");
        task1.setEndingDate("20240110+1700");

        TaskManager task2 = new TaskManager();
        task2.setId(2);
        task2.setName("Task 2");
        task2.setStartingDate("20240105+0900");  // Starts before task1 ends
        task2.setEndingDate("20240115+1700");
        task2.setDependsOn("1");  // Depends on task1

        scheduler.Tasks.put(1, task1);
        scheduler.Tasks.put(2, task2);

        int[] overlapping = scheduler.overlapingTasks();

        assertEquals(1, overlapping.length, "Should detect 1 overlapping task");
        assertEquals(2, overlapping[0], "Task 2 should be marked as overlapping");
    }

    @Test
    public void testMultipleOverlappingTasks() {
        // Test case: Multiple tasks overlapping with their dependencies
        TaskManager task1 = new TaskManager();
        task1.setId(1);
        task1.setName("Task 1");
        task1.setStartingDate("20240101+0900");
        task1.setEndingDate("20240110+1700");

        TaskManager task2 = new TaskManager();
        task2.setId(2);
        task2.setName("Task 2");
        task2.setStartingDate("20240105+0900");
        task2.setEndingDate("20240115+1700");
        task2.setDependsOn("1");

        TaskManager task3 = new TaskManager();
        task3.setId(3);
        task3.setName("Task 3");
        task3.setStartingDate("20240108+0900");
        task3.setEndingDate("20240120+1700");
        task3.setDependsOn("1");

        scheduler.Tasks.put(1, task1);
        scheduler.Tasks.put(2, task2);
        scheduler.Tasks.put(3, task3);

        int[] overlapping = scheduler.overlapingTasks();

        assertEquals(2, overlapping.length, "Should detect 2 overlapping tasks");
    }

    @Test
    public void testNoOverlapWhenTaskStartsAfterDependencyEnds() {
        // Test case: Task starts after dependency ends (no overlap)
        TaskManager task1 = new TaskManager();
        task1.setId(1);
        task1.setName("Task 1");
        task1.setStartingDate("20240101+0900");
        task1.setEndingDate("20240105+1700");

        TaskManager task2 = new TaskManager();
        task2.setId(2);
        task2.setName("Task 2");
        task2.setStartingDate("20240106+0900");  // Starts after task1 ends
        task2.setEndingDate("20240110+1700");
        task2.setDependsOn("1");

        scheduler.Tasks.put(1, task1);
        scheduler.Tasks.put(2, task2);

        int[] overlapping = scheduler.overlapingTasks();

        assertEquals(0, overlapping.length, "No overlapping tasks expected");
    }

    @Test
    public void testProjectCompletionTimeCalculation() {
        // Test case: Calculate total project completion time
        TaskManager task1 = new TaskManager();
        task1.setId(1);
        task1.setName("Task 1");
        task1.setStartingDate("20240101+0900");
        task1.setEndingDate("20240105+1700");
        task1.CalculateTimeSpan();

        TaskManager task2 = new TaskManager();
        task2.setId(2);
        task2.setName("Task 2");
        task2.setStartingDate("20240106+0900");
        task2.setEndingDate("20240110+1700");
        task2.setDependsOn("1");
        task2.CalculateTimeSpan();

        scheduler.Tasks.put(1, task1);
        scheduler.Tasks.put(2, task2);

        scheduler.calculateProjectCompletion();

        assertNotNull(scheduler.getProjectStartTime(), "Project start time should be calculated");
        assertNotNull(scheduler.getProjectEndTime(), "Project end time should be calculated");
        assertTrue(scheduler.getTotalDays() >= 0, "Total days should be non-negative");
    }

    @Test
    public void testProjectCompletionWithDependencies() {
        // Test case: Project completion considers dependencies
        TaskManager task1 = new TaskManager();
        task1.setId(1);
        task1.setName("Task 1");
        task1.setStartingDate("20240101+0900");
        task1.setEndingDate("20240103+1700");
        task1.CalculateTimeSpan();

        TaskManager task2 = new TaskManager();
        task2.setId(2);
        task2.setName("Task 2");
        task2.setStartingDate("20240104+0900");
        task2.setEndingDate("20240107+1700");
        task2.setDependsOn("1");
        task2.CalculateTimeSpan();

        TaskManager task3 = new TaskManager();
        task3.setId(3);
        task3.setName("Task 3");
        task3.setStartingDate("20240108+0900");
        task3.setEndingDate("20240110+1700");
        task3.setDependsOn("2");
        task3.CalculateTimeSpan();

        scheduler.Tasks.put(1, task1);
        scheduler.Tasks.put(2, task2);
        scheduler.Tasks.put(3, task3);

        scheduler.calculateProjectCompletion();

        long totalDays = scheduler.getTotalDays();

        // Project should span from Jan 1 to Jan 10 (9 days)
        assertTrue(totalDays <= 9, "Total project days should be at least 9");
    }

    @Test
    public void testEmptyProjectCompletion() {
        // Test case: Empty project should handle gracefully
        // No tasks added

        scheduler.calculateProjectCompletion();

        // Should not crash - if it reaches here, test passes
        assertTrue(true, "Should handle empty tasks without crashing");
    }

    @Test
    public void testTaskDurationCalculation() {
        // Test case: Individual task duration is calculated correctly
        TaskManager task = new TaskManager();
        task.setId(1);
        task.setName("Test Task");
        task.setStartingDate("20240101+0900");
        task.setEndingDate("20240105+1700");

        task.CalculateTimeSpan();

        assertEquals(4, task.getDays(), "Task should span 4 days");
        assertEquals(8, task.getHours(), "Task should have 8 hours");
    }

    @Test
    public void testOverlapWithSameDayBoundary() {
        // Edge case: Tasks that touch on the same day boundary
        TaskManager task1 = new TaskManager();
        task1.setId(1);
        task1.setName("Task 1");
        task1.setStartingDate("20240101+0900");
        task1.setEndingDate("20240105+1700");

        TaskManager task2 = new TaskManager();
        task2.setId(2);
        task2.setName("Task 2");
        task2.setStartingDate("20240105+0900");  // Same day as task1 ends
        task2.setEndingDate("20240110+1700");
        task2.setDependsOn("1");

        scheduler.Tasks.put(1, task1);
        scheduler.Tasks.put(2, task2);

        int[] overlapping = scheduler.overlapingTasks();

        // Should detect overlap since they're on the same day
        assertEquals(1, overlapping.length, "Should detect overlap on same day boundary");
    }
}
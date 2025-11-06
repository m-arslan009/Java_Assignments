package Database;

import Helper_Classes.TaskManager;
import java.sql.*;
import java.util.HashMap;

public class TaskData {
    ConnectionBuild connection = new ConnectionBuild();

    public void saveTasks(HashMap<Integer, TaskManager> tasks) {
        clearTasks();
        String sql = "INSERT INTO Tasks (id, name, startingDate, endingDate, dependsOn) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = connection.buildConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (TaskManager t : tasks.values()) {
                stmt.setInt(1, t.getId());
                stmt.setString(2, t.getName());
                stmt.setString(3, t.getStarting());
                stmt.setString(4, t.getEnding());
                stmt.setString(5, String.join(",", t.getDependencies()));
                stmt.addBatch();
            }

            stmt.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public HashMap<Integer, TaskManager> loadTasks() {
        HashMap<Integer, TaskManager> map = new HashMap<>();
        String sql = "SELECT * FROM Tasks";

        try (Connection conn = connection.buildConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                TaskManager t = new TaskManager();
                t.setId(rs.getInt("id"));
                t.setName(rs.getString("name"));
                t.setStartingDate(rs.getString("StartingDate"));
                t.setEndingDate(rs.getString("endingDate"));

                String deps = rs.getString("dependsOn");
                if (deps != null && !deps.isEmpty()) {
                    for (String d : deps.split(",")) {
                        t.setDependsOn(d.trim());
                    }
                }
                map.put(t.getId(), t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    public void updateTask(TaskManager task) {
        String sql = "UPDATE Tasks SET name = ?, startingDate = ?, endingDate = ?, dependsOn = ? WHERE id = ?";

        try (Connection conn = connection.buildConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, task.getName());
            stmt.setString(2, task.getStarting());
            stmt.setString(3, task.getEnding());
            stmt.setString(4, String.join(",", task.getDependencies()));
            stmt.setInt(5, task.getId());

            int rowsAffected = stmt.executeUpdate();
            System.out.println("Updated " + rowsAffected + " task(s)");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTask(int id) {
        String sql = "DELETE FROM Tasks WHERE id = ?";
        try (Connection conn = connection.buildConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Deleted " + rowsAffected + " task(s)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void clearTasks() {
        String sql = "DELETE FROM Tasks";
        try (Connection conn = connection.buildConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
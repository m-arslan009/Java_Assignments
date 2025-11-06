package Database;

import Helper_Classes.ResourceManager;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;

public class ResourceData {
    ConnectionBuild connection = new ConnectionBuild();

    // ✅ Save all resources into the database
    public void saveResources(HashMap<String, ResourceManager> resources) {
        // First clear existing resources to avoid duplicates
        clearResources();

        String sql = "INSERT INTO Resources (name, resourceId, contribution) VALUES (?, ?, ?)";

        try (Connection conn = connection.buildConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false); // ensure manual commit for batch

            for (ResourceManager rm : resources.values()) {
                for (Map.Entry<Integer, Integer> entry : rm.getResource().entrySet()) {
                    stmt.setString(1, rm.name);
                    stmt.setInt(2, entry.getKey());
                    stmt.setInt(3, entry.getValue());
                    stmt.addBatch();
                }
            }

            int[] result = stmt.executeBatch();
            conn.commit(); // commit all insertions
            System.out.println(result.length + " resource rows inserted successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database Save Error: " + e.getMessage());
        }
    }

    // ✅ Load resources from database
    public HashMap<String, ResourceManager> loadResources() {
        HashMap<String, ResourceManager> map = new HashMap<>();
        String sql = "SELECT * FROM Resources";

        try (Connection conn = connection.buildConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String name = rs.getString("name");
                int resourceId = rs.getInt("resourceId");
                int contribution = rs.getInt("contribution");

                ResourceManager r = map.getOrDefault(name, new ResourceManager());
                r.setName(name);
                r.getResource().put(resourceId, contribution);
                map.put(name, r);
            }

            System.out.println("Loaded " + map.size() + " resources from database.");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database Load Error: " + e.getMessage());
        }
        return map;
    }

    // ✅ Delete a specific resource by name
    public void deleteResource(String name) {
        String sql = "DELETE FROM Resources WHERE name = ?";

        try (Connection conn = connection.buildConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            int rows = stmt.executeUpdate();
            System.out.println(rows + " resource(s) deleted.");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Delete Error: " + e.getMessage());
        }
    }

    // ✅ Remove a specific task from all resources
    public void removeTaskFromResources(int taskId) {
        String sql = "DELETE FROM Resources WHERE resourceId = ?";

        try (Connection conn = connection.buildConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, taskId);
            int rows = stmt.executeUpdate();
            System.out.println(rows + " resource assignment(s) deleted for task " + taskId);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Delete Task from Resources Error: " + e.getMessage());
        }
    }

    // ✅ Update a specific resource in the database
    public void updateResource(ResourceManager resource) {
        // Delete existing entries for this resource
        deleteResource(resource.name);

        // Insert updated entries
        String sql = "INSERT INTO Resources (name, resourceId, contribution) VALUES (?, ?, ?)";

        try (Connection conn = connection.buildConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            for (Map.Entry<Integer, Integer> entry : resource.getResource().entrySet()) {
                stmt.setString(1, resource.name);
                stmt.setInt(2, entry.getKey());
                stmt.setInt(3, entry.getValue());
                stmt.addBatch();
            }

            int[] result = stmt.executeBatch();
            conn.commit();
            System.out.println(result.length + " resource rows updated for " + resource.name);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Update Resource Error: " + e.getMessage());
        }
    }

    // ✅ Clear the entire table
    public void clearResources() {
        String sql = "DELETE FROM Resources";

        try (Connection conn = connection.buildConnection();
             Statement stmt = conn.createStatement()) {

            int rows = stmt.executeUpdate(sql);
            System.out.println(rows + " resource(s) cleared.");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Clear Error: " + e.getMessage());
        }
    }

    public boolean hasData() {
        String sql = "SELECT COUNT(*) as total FROM Resources";

        try (Connection conn = connection.buildConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                int count = rs.getInt("total");
                System.out.println("Resources table has " + count + " rows.");
                return count > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error checking resource data: " + e.getMessage());
        }
        return false;
    }
}
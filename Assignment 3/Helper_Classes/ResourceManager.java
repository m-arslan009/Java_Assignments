package Helper_Classes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class ResourceManager {
    public String name;
    public HashMap<Integer, Integer> resources;

    public ResourceManager() {
        this.name = "";
        resources = new HashMap<>();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSourceData(String obj) {
        String[] data = obj.split(":");
        int Id = Integer.parseInt(data[0].trim());
        int Contribution = Integer.parseInt(data[1].trim());
        resources.put(Id, Contribution);
    }

    public HashMap<Integer, Integer> getResource() {
        return this.resources;
    }

    public void PrintResources() {
        System.out.println("Name: " +this.name);
        for(Map.Entry<Integer, Integer> entry : resources.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
    }
}

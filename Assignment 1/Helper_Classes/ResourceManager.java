package Helper_Classes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ResourceManager {
    String name;
//    ArrayList<String> resource;
//    ArrayList<String> allocation;
    HashMap<Integer, Integer> resources;

    public ResourceManager() {
        this.name = "";
        resources = new HashMap<>();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSourceData(String obj) {
        String[] data = obj.split(":");
        resources.put(Integer.parseInt(data[0]), Integer.parseInt(data[1]));
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

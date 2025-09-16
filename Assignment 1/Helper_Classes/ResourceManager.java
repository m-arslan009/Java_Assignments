package Helper_Classes;
import java.util.ArrayList;

public class ResourceManager {
    String name;
    ArrayList<String> resource;
    ArrayList<String> allocation;

    public ResourceManager() {
        this.name = "";
        this.resource = new ArrayList<String>();
        this.allocation = new ArrayList<String>();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSourceData(String obj) {
        String[] data = obj.split(":");
        resource.add(data[0]);
        allocation.add(data[1]);
    }

    public ArrayList<String> getResource() {
        return this.resource;
    }

    public ArrayList<String> getAllocation() {
        return this.allocation;
    }

    public void PrintResources() {
        System.out.println("Name: " +this.name);
        String res, alloc;
        for(int i = 0; i < this.resource.size(); i++) {
            res = this.resource.get(i);
            alloc = this.allocation.get(i);
            System.out.println(res + ":" + alloc);
        }
    }
}

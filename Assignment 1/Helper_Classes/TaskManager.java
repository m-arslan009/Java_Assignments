package Helper_Classes;

import javax.swing.text.DateFormatter;
import java.text.DateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class TaskManager {
    int id;
    String name;
    String startingDate;
    String endingDate;
    String startingTime;
    String endingTime;
    public class TotalDuration {
        long days;
        long Hours;
        long Min;

        public TotalDuration(long days, long Hours, long Min) {
            this.days = days;
            this.Hours = Hours;
            this.Min = Min;
        }
    }
    TotalDuration timeSpan;
    ArrayList<String> dependsOn;

    public TaskManager() {
        id = 0;
        name = "";
        startingDate = "";
        endingDate = "";
        dependsOn = new ArrayList<String>();
        timeSpan = null;
    }

    void calcDifferenceBetweenDates() {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");

        LocalDate date1 = LocalDate.parse(this.startingDate.trim(), df);
        LocalDate date2 = LocalDate.parse(this.endingDate.trim(), df);
        LocalTime time1 = LocalTime.parse(this.startingTime.trim(), tf);
        LocalTime time2 = LocalTime.parse(this.endingTime.trim(), tf);

        LocalDateTime start = LocalDateTime.of(date1, time1);
        LocalDateTime end = LocalDateTime.of(date2, time2);

        Duration taskDuration = Duration.between(start, end);

        long days = taskDuration.toDays();
        long hours = taskDuration.toHours() % 24;
        long minutes = taskDuration.toMinutes() % 60;
        this.timeSpan = new TotalDuration(days, hours, minutes);
    }

    public void CalculateTimeSpan() {
        calcDifferenceBetweenDates();
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    String formatDate(String val) {
        val = val.trim();
        if (val.length() < 8) {
            return val;
        }
        String Year = val.substring(0, 4);   // fixed
        String Month = val.substring(4, 6);
        String Day = val.substring(6, 8);

        return Year + "-" + Month + "-" + Day;
    }

    String formatTime(String val) {
        val = val.trim();
        if (val.length() != 4) {
            return val;
        }
        String hours = val.substring(0, 2);
        String min = val.substring(2, 4);

        return hours + ":" + min;
    }


    public void setStartingDate(String startingDate) {
        String delimiter = "[+]";
        String[] date = startingDate.split(delimiter);
        this.startingDate = formatDate(date[0]);
        this.startingTime = formatTime(date[1]);
    }

    public void setEndingDate(String endingDate) {
        String delimiter = "[+]";
        String[] date = endingDate.split(delimiter);
        this.endingDate = formatDate(date[0]);
        this.endingTime = formatTime(date[1]);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStartingDate() {
        return startingDate;
    }

    public String getEndingDate() {
        return endingDate;
    }

    public String getStartingTime() {
        return startingTime;
    }

    public void setStartingTime(String startingTime) {
        this.startingTime = startingTime;
    }

    public String getEndingTime() {
        return endingTime;
    }

    public void setEndingTime(String endingTime) {
        this.endingTime = endingTime;
    }

    public void setDependsOn(String dependsOn) {
        this.dependsOn.add(dependsOn);
    }

    public long getDays() {
        return this.timeSpan.days;
    }



    public long getHours() {
        return this.timeSpan.Hours;
    }


    public long getMin() {
        return this.timeSpan.Min;
    }

    public ArrayList<String> getDependencies() {
        return dependsOn;
    }

    public void PrintTaskManager() {
        System.out.println("---------- TASK MANAGER ----------");
        System.out.println(this.id);
        System.out.println(this.name);
        System.out.println(this.startingDate);
        System.out.println(this.endingDate);
        System.out.println(this.startingTime);
        System.out.println(this.endingTime);
        System.out.print("Sources on which it depends on:[");
        int flag = 0;
        for(String obj : dependsOn) {
            System.out.print(obj+(flag++ != dependsOn.size() - 1 ?  "," : ""));
        }
        System.out.println("]");
        System.out.print("Duration of this task: ");
        if(this.timeSpan == null) {
            calcDifferenceBetweenDates();
        }
        System.out.println(this.timeSpan.days + " Days" + " " + this.timeSpan.Hours + " Hours "+ " " + this.timeSpan.Min + " Minutes");

        System.out.println();
    }
}

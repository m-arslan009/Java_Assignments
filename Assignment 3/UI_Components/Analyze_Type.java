package UI_Components;

import javax.swing.*;
import java.awt.event.ActionListener;

public class Analyze_Type extends JPanel {
    RadioButton Project_Timing;
    RadioButton Overlapping_Tasks;
    RadioButton Resources_and_team;
    RadioButton Effort_Breakdown;
    ButtonGroup RadioOptions;

    public Analyze_Type() {
        this.setLayout(new BoxLayout(this,  BoxLayout.Y_AXIS));
        Project_Timing = new RadioButton("Project Timing");
        Overlapping_Tasks = new RadioButton("Overlapping Tasks");
        Resources_and_team = new RadioButton("Resources and team");
        Effort_Breakdown = new RadioButton("Effort breakdown: Resource-wise");

        RadioOptions = new ButtonGroup();
        RadioOptions.add(Project_Timing);
        RadioOptions.add(Overlapping_Tasks);
        RadioOptions.add(Resources_and_team);
        RadioOptions.add(Effort_Breakdown);


        this.add(Project_Timing);
        this.add(Overlapping_Tasks);
        this.add(Resources_and_team);
        this.add(Effort_Breakdown);

    }

    public ButtonGroup getRadioOptions() {
        return RadioOptions;
    }

    public RadioButton getEffort_Breakdown() {
        return Effort_Breakdown;
    }

    public RadioButton getResources_and_team() {
        return Resources_and_team;
    }

    public RadioButton getOverlapping_Tasks() {
        return Overlapping_Tasks;
    }

    public RadioButton getProject_Timing() {
        return Project_Timing;
    }

    public void isProjectTimingSelected(JPanel component) {
        Project_Timing.isChecked(component);
    }

    public void isOverlappingTasksSelected(JPanel component) {
        Overlapping_Tasks.isChecked(component);

    }

    public void isResources_and_teamSelected(JPanel component) {
        Resources_and_team.isChecked(component);
    }

}

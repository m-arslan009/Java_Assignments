package UI_Components;

import javax.swing.*;

public class ProjectCompletion extends JPanel {
    JPanel Completion;
    JPanel Duration;
    JTextField durationField;
    JTextField dateField;
    public ProjectCompletion() {
        JLabel dateLabel = new JLabel("Project Completion: ");
        dateField = new JTextField();

        JLabel durationLabel = new JLabel("Total Duration: ");
        durationField = new JTextField();

        Completion = new JPanel();
        Duration = new JPanel();

        Completion.add(dateLabel);
        Completion.add(dateField);

        Duration.add(durationLabel);
        Duration.add(durationField);

        this.setSize(400, 400);
        this.add(Completion);
        this.add(Duration);
        super.setVisible(true);

        durationField.setEditable(false);
        dateField.setEditable(false);
    }

    public void setData(String Date, String Duration) {
        durationField.setText(Duration);
        dateField.setText(Date);
    }

}

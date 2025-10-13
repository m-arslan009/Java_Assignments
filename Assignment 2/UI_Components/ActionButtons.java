package UI_Components;

import javax.swing.*;
import java.awt.*;

public class ActionButtons extends JPanel {
    JButton Upload_Task;
    JButton Upload_Resource;
    JButton Analyze;
    JButton Visualize;

    public ActionButtons() {
        Upload_Task = new Button("Upload Task");
        Upload_Resource = new Button("Upload Resource");
        Analyze = new Button("Analyze");
        Visualize = new Button("Visualize");

        this.add(Upload_Task);
        this.add(Upload_Resource);
        this.add(Analyze);
        this.add(Visualize);
    }

    public JButton getVisualize() {
        return Visualize;
    }

    public JButton getAnalyze() {
        return Analyze;
    }

    public JButton getUpload_Resource() {
        return Upload_Resource;
    }

    public JButton getUpload_Task() {
        return Upload_Task;
    }


}

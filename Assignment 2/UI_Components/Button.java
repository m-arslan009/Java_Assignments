package UI_Components;

import javax.swing.*;
import java.awt.event.ActionListener;

public class Button extends JButton {
    public Button(String label) {
        super(label);
    }

    @Override
    public void addActionListener(ActionListener l) {
        super.addActionListener(l);
    }
}

package UI_Components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class Header extends JPanel {
    public JPanel header;
    JPanel Buttons;
    JPanel InputPanel;

    public Button New;
    public Button Save;
    public Button Load;

    public Header() {
         super();
         this.setLayout(new BorderLayout(8,4));

         Buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8,4));
         InputPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8,4));

        New = new Button("New");
        Save = new Button("Save");
        Load = new Button("Load");
        JLabel title = new JLabel("Project: ");
        JTextField name = new JTextField(20);

        Buttons.add(New);
        Buttons.add(Save);
        Buttons.add(Load);

        InputPanel.add(title);
        InputPanel.add(name);

       this.add(Buttons, BorderLayout.WEST);
       this.add(InputPanel, BorderLayout.EAST);

       this.setVisible(true);
       this.setBounds(0, 0, 400, 200);
    }

    public JButton getNew() {
        return New;
    }

    public JButton getSave() {
        return Save;
    }

    public JButton getLoad() {
        return Load;
    }
}

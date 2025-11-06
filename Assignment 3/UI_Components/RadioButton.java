package UI_Components;

import javax.swing.*;

public class RadioButton extends JRadioButton {
    public RadioButton(String Label) {
        super(Label);
    }

    public void isChecked(JPanel obj) {
        if (super.isSelected()) {
            obj.setVisible(true);
        } else {
            obj.setVisible(false);
        }
        obj.revalidate();
        obj.repaint();
    }

}

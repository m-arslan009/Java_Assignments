package UI_Components;

import javax.swing.*;
import java.awt.*;

public class Rectangle extends JPanel {
    private String name;
    private int start;
    private int duration;
    private Color backgroundColor;
    private Color textColor;

    public Rectangle(String name, int start, int duration, Color backgroundColor, Color textColor) {
        this.name = name;
        this.start = start;
        this.duration = duration;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
        setPreferredSize(new Dimension(duration + 20, 50));
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw rectangle
        g2.setColor(backgroundColor);
        g2.fillRoundRect(start, 10, duration, 30, 10, 10);

        // Draw border
        g2.setColor(backgroundColor.darker());
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(start, 10, duration, 30, 10, 10);

        // Fit text inside rectangle
        g2.setColor(textColor);
        FontMetrics fm = g2.getFontMetrics();
        String text = name;

        int textWidth = fm.stringWidth(text);
        if (textWidth > duration - 10) {
            // shrink text to fit
            float ratio = (float)(duration - 10) / textWidth;
            Font f = g2.getFont().deriveFont(g2.getFont().getSize2D() * ratio);
            g2.setFont(f);
            fm = g2.getFontMetrics();
        }

        int textX = start + 5;
        int textY = 10 + ((30 - fm.getHeight()) / 2) + fm.getAscent();
        g2.drawString(text, textX, textY);
    }
}

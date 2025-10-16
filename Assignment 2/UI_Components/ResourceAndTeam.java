package UI_Components;

import javax.swing.*;
import java.awt.*;

public class ResourceAndTeam extends JPanel {
    JTextArea resourceInfoArea;
    JScrollPane scrollPane;

    public ResourceAndTeam() {
        super.setLayout(new BorderLayout());

        // Create text area for displaying resource information
        resourceInfoArea = new JTextArea();
        resourceInfoArea.setEditable(false);
        resourceInfoArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        resourceInfoArea.setMargin(new Insets(10, 10, 10, 10));

        // Wrap in scroll pane
        scrollPane = new JScrollPane(resourceInfoArea);
        scrollPane.setPreferredSize(new Dimension(380, 150));

        super.add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Display resource duration information
     * @param resourceDurations Array of StringBuilder containing resource duration data
     */
    public void displayResourceDurations(StringBuilder[] resourceDurations) {
        StringBuilder content = new StringBuilder();
        content.append("=== RESOURCE EFFORT BREAKDOWN ===\n\n");

        if(resourceDurations == null || resourceDurations.length == 0) {
            content.append("No resource data available.\n");
        } else {
            for(int i = 0; i < resourceDurations.length; i++) {
                if(resourceDurations[i] != null) {
                    content.append((i + 1)).append(". ").append(resourceDurations[i]).append("\n");
                }
            }
        }

        resourceInfoArea.setText(content.toString());
    }

    /**
     * Display team information (resources working together)
     * @param teamTaskIds Array of task IDs where resources work together
     */
    public void displayTeamInfo(int[] teamTaskIds) {
        StringBuilder content = new StringBuilder();
        content.append("=== RESOURCES WORKING AS TEAM ===\n\n");

        if(teamTaskIds == null || teamTaskIds.length == 0) {
            content.append("No team collaboration found.\n");
            content.append("(Resources are working independently on separate tasks)\n");
        } else {
            content.append("Tasks where multiple resources collaborate:\n\n");
            for(int i = 0; i < teamTaskIds.length; i++) {
                if(teamTaskIds[i] != 0) {
                    content.append("  • Task ID: ").append(teamTaskIds[i]).append("\n");
                }
            }
            content.append("\nTotal collaborative tasks: ").append(teamTaskIds.length);
        }

        resourceInfoArea.setText(content.toString());
    }

    /**
     * Clear the display
     */
    public void clearDisplay() {
        resourceInfoArea.setText("");
    }
}
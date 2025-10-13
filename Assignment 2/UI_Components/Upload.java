package UI_Components;

import javax.swing.*;
import java.io.File;

public class Upload extends JPanel {
    JFileChooser uploadFile;
    File selectedFile;
    public Upload() {
        this.selectedFile = null;
        uploadFile = new JFileChooser();
        uploadFile.setFileSelectionMode(JFileChooser.FILES_ONLY);
        uploadFile.setMultiSelectionEnabled(false);
        uploadFile.setAcceptAllFileFilterUsed(false);
        uploadFile.setDialogType(JFileChooser.OPEN_DIALOG);
    }

    public int OpenDialog() {
        return uploadFile.showOpenDialog(null);
    }

    public void setSelectedFile() {
        this.selectedFile = uploadFile.getSelectedFile();
    }

    public String getSelectedFilePath() {
        return selectedFile.getAbsolutePath();
    }
}

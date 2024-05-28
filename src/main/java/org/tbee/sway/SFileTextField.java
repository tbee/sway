package org.tbee.sway;

import org.tbee.sway.format.FileFormat;
import org.tbee.sway.support.IconRegistry;

import javax.swing.JFileChooser;
import java.io.File;

public class SFileTextField extends STextField<File> {

    public SFileTextField() {
        super(new FileFormat());
        icon(IconRegistry.find(IconRegistry.SwayInternallyUsedIcon.TEXTFIELD_POPUP));
        onIconClick(evt -> showFileChooser());
    }

    private void showFileChooser() {
        JFileChooser fc = new JFileChooser();
        int returnVal = fc.showDialog(this, "Select");
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            setValue(file);
        }
    }

    public static SFileTextField of() {
        return new SFileTextField();
    }
}

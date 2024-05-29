package org.tbee.sway;

import org.tbee.sway.binding.BindingEndpoint;
import org.tbee.sway.format.FileFormat;
import org.tbee.sway.support.IconRegistry;

import javax.swing.JFileChooser;
import java.io.File;

public class SFileTextField extends STextField<File> {

    private final FileFormat fileFormat;

    public SFileTextField() {
        super(new FileFormat());
        fileFormat = (FileFormat) super.getFormat();

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

    // ==============================================
    // FLUENT API

    public boolean getMustExist() {
        return fileFormat.getMustExist();
    }
    public void setMustExist(boolean v) {
        boolean before = fileFormat.getMustExist();
        fileFormat.setMustExist(v);
        firePropertyChange(MUSTEXIST, before, v);
    }
    public SFileTextField mustExist(boolean v) {
        setMustExist(v);
        return this;
    }
    final static public String MUSTEXIST = "mustExist";
    public BindingEndpoint<Boolean> mustExist() {
        return BindingEndpoint.of(this, MUSTEXIST);
    }


    // ==============================================
    // OF

    public static SFileTextField of() {
        return new SFileTextField();
    }
}

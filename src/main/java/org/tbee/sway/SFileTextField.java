package org.tbee.sway;

import org.tbee.sway.binding.BindingEndpoint;
import org.tbee.sway.format.FileFormat;
import org.tbee.sway.format.FileFormat.AllowedType;

import javax.swing.JFileChooser;
import java.io.File;

/**
 * SFileTextField is nothing more than a preconfigured STextField
 */
public class SFileTextField extends STextField<File> {

    private final FileFormat fileFormat;

    public SFileTextField() {
        super(new FileFormat());
        fileFormat = (FileFormat) super.getFormat();

        icon(SIconRegistry.find(SIconRegistry.SwayInternallyUsedIcon.TEXTFIELD_POPUP));
        onIconClick(evt -> showFileChooser());
    }

    private void showFileChooser() {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setFileSelectionMode(
            switch(getAllowedType()) {
                case ALL -> JFileChooser.FILES_AND_DIRECTORIES;
                case FILE -> JFileChooser.FILES_ONLY;
                case DIR -> JFileChooser.DIRECTORIES_ONLY;
            }
        );

        int returnVal = jFileChooser.showDialog(this, "Select");
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = jFileChooser.getSelectedFile();
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


    public AllowedType getAllowedType() {
        return fileFormat.getAllowedType();
    }
    public void setAllowedType(AllowedType v) {
        AllowedType before = fileFormat.getAllowedType();
        fileFormat.setAllowedType(v);
        firePropertyChange(ALLOWEDTYPE, before, v);
    }
    public SFileTextField allowedType(AllowedType v) {
        setAllowedType(v);
        return this;
    }
    final static public String ALLOWEDTYPE = "allowedType";
    public BindingEndpoint<AllowedType> allowedType() {
        return BindingEndpoint.of(this, ALLOWEDTYPE);
    }

    // ==============================================
    // OF

    public static SFileTextField of() {
        return new SFileTextField();
    }
}

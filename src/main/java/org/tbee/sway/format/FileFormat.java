package org.tbee.sway.format;

import javax.swing.JFileChooser;
import java.io.File;

public class FileFormat implements Format<File> {

    private boolean mustExist = false;

    public enum AllowedType {
        FILE, DIR, ALL;
    }
    private AllowedType allowedType = AllowedType.ALL;

    @Override
    public String toString(File value) {
        return value == null ? "" : value.getAbsolutePath();
    }

    @Override
    public File toValue(String string) {
        File file = string.isBlank() ? null : new File(string);

        if (file != null) {
            if (mustExist && !file.exists()) {
                throw new FormatException("Does not exist: " + file);
            }
            if (allowedType == AllowedType.FILE && file.exists() && !file.isFile()) {
                throw new FormatException("Not a file: " + file);
            }
            if (allowedType == AllowedType.DIR && file.exists() && !file.isDirectory()) {
                throw new FormatException("Not a directory: " + file);
            }
        }

        return file;
    }

    static public FileFormat of() {
        return new FileFormat();
    }

    // ==============================================
    // FLUENT API

    public boolean getMustExist() {
        return mustExist;
    }
    public void setMustExist(boolean v) {
        this.mustExist = v;
    }
    public FileFormat mustExist(boolean v) {
        setMustExist(v);
        return this;
    }

    public AllowedType getAllowedType() {
        return allowedType;
    }
    public void setAllowedType(AllowedType v) {
        this.allowedType = v;
    }
    public FileFormat allowedType(AllowedType v) {
        setAllowedType(v);
        return this;
    }


    // ==============================================
    // EDITOR

    @Override
    public Editor<File> editor() {
        return (owner, value, callback) -> {

            JFileChooser jFileChooser = new JFileChooser();
            if (value != null) {
                jFileChooser.setSelectedFile(value);
            }
            jFileChooser.setFileSelectionMode(
                switch(getAllowedType()) {
                    case ALL -> JFileChooser.FILES_AND_DIRECTORIES;
                    case FILE -> JFileChooser.FILES_ONLY;
                    case DIR -> JFileChooser.DIRECTORIES_ONLY;
                }
            );

            if (jFileChooser.showOpenDialog(owner) == JFileChooser.APPROVE_OPTION) {
                callback.accept(jFileChooser.getSelectedFile());
            }
        };
    }
}
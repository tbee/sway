package org.tbee.sway.format;

import java.io.File;

public class FileFormat implements Format<File> {

    private boolean mustExist = false;

    @Override
    public String toString(File value) {
        return value == null ? "" : value.getAbsolutePath();
    }

    @Override
    public File toValue(String string) {
        File file = string.isBlank() ? null : new File(string);

        if (file != null) {
            if (mustExist && !file.exists()) {
                throw new FormatException("File does not exist: " + file);
            }
        }

        return file;
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
}
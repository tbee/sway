package org.tbee.sway.format;

import java.io.File;

public class FileFormat implements Format<File> {

    @Override
    public String toString(File value) {
        return value == null ? "" : value.getAbsolutePath();
    }

    @Override
    public File toValue(String string) {
        return string.isBlank() ? null : new File(string);
    }
}
package org.tbee.sway.format;

import javax.swing.*;

public class StringFormat implements Format<String> {

    private final boolean blankIsNull;
    public StringFormat(boolean blankIsNull) {
        this.blankIsNull = blankIsNull;
    }
    @Override
    public String toString(String value) {
        return value == null && blankIsNull ? "" : value;
    }

    @Override
    public String toValue(String string) {
        return string.isBlank() && blankIsNull ? null : string;
    }

    @Override
    public int columns() {
        return 25;
    }

    @Override
    public int horizontalAlignment() {
        return SwingConstants.LEADING;
    }
}
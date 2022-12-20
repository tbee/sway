package org.tbee.sway.format;

import javax.swing.*;

public class StringFormat implements Format<String> {

    private final boolean emptyIsNull;
    public StringFormat(boolean emptyIsNull) {
        this.emptyIsNull = emptyIsNull;
    }
    @Override
    public String toString(String value) {
        return value == null && emptyIsNull ? "" : value;
    }

    @Override
    public String toValue(String string) {
        return string.isEmpty() && emptyIsNull ? null : string;
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
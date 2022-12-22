package org.tbee.sway.format;

import javax.swing.*;

public class LongFormat implements Format<Long> {

    @Override
    public String toString(Long value) {
        return value == null ? "" : value.toString();
    }

    @Override
    public Long toValue(String string) {
        return string.isBlank() ? null : Long.parseLong(string);
    }

    @Override
    public int columns() {
        return ("" + Long.MIN_VALUE).length();
    }

    @Override
    public int horizontalAlignment() {
        return SwingConstants.TRAILING;
    }
}
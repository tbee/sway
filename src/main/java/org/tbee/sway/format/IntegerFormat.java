package org.tbee.sway.format;

import org.tbee.sway.support.HAlign;

public class IntegerFormat implements Format<Integer> {

    @Override
    public String toString(Integer value) {
        return value == null ? "" : value.toString();
    }

    @Override
    public Integer toValue(String string) {
        return string.isBlank() ? null : Integer.parseInt(string);
    }

    @Override
    public int columns() {
        return ("" + Integer.MIN_VALUE).length();
    }

    @Override
    public HAlign horizontalAlignment() {
        return HAlign.TRAILING;
    }
}
package org.tbee.sway.format;

import org.tbee.sway.support.HAlign;

public class DoubleFormat implements Format<Double> {

    @Override
    public String toString(Double value) {
        return value == null ? "" : value.toString();
    }

    @Override
    public Double toValue(String string) {
        return string.isBlank() ? null : Double.parseDouble(string);
    }

    @Override
    public int columns() {
        return ("" + Double.MIN_VALUE).length();
    }

    @Override
    public HAlign horizontalAlignment() {
        return HAlign.TRAILING;
    }
}
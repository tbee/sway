package org.tbee.sway.format;

import org.tbee.sway.support.HAlign;

import java.text.DecimalFormat;
import java.text.ParseException;

public class IntegerFormat implements Format<Integer> {

    public static final String DEFAULT_PATTERN = "#";
    private final DecimalFormat formatter;
    private final String pattern;

    public IntegerFormat() {
        this(DEFAULT_PATTERN);
    }

    public IntegerFormat(String pattern) {
        this.pattern = pattern;
        formatter = new DecimalFormat(pattern);
    }

    @Override
    public String toString(Integer value) {
        return value == null ? "" : formatter.format(value);
    }

    @Override
    public Integer toValue(String string) {
        try {
            return string.isBlank() ? null : formatter.parse(string).intValue();
        }
        catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int columns() {
        return (pattern == DEFAULT_PATTERN ? ("" + Integer.MIN_VALUE).length() : pattern.length());
    }

    @Override
    public HAlign horizontalAlignment() {
        return HAlign.TRAILING;
    }
}
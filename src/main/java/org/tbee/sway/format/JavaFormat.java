package org.tbee.sway.format;

import org.tbee.sway.support.HorizontalAlignment;

import java.text.ParseException;

public class JavaFormat<T> implements Format<T> {

    private final java.text.Format format;
    private final int columns;
    private final HorizontalAlignment horizontalAlignment;

    public JavaFormat(java.text.Format format, int columns, HorizontalAlignment horizontalAlignment) {
        this.format = format;
        this.columns = columns;
        this.horizontalAlignment = horizontalAlignment;
    }

    @Override
    public String toString(T value) {
        return value == null ? "" : format.format(value);
    }

    @Override
    public T toValue(String string) {
        if (string.isBlank()) {
            return null;
        }
        try {
            return (T)format.parseObject(string);
        }
        catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int columns() {
        return this.columns;
    }

    @Override
    public HorizontalAlignment horizontalAlignment() {
        return this.horizontalAlignment;
    }
}
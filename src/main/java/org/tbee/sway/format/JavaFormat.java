package org.tbee.sway.format;

import java.text.ParseException;

public class JavaFormat<T> implements Format<T> {

    private final java.text.Format format;
    private final int columns;
    private final int horizontalAlignment;

    public JavaFormat(java.text.Format format, int columns, int horizontalAlignment) {
        this.format = format;
        this.columns = columns;
        this.horizontalAlignment = horizontalAlignment;
    }

    @Override
    public String toString(T value) {
        return format.format(value);
    }

    @Override
    public T toValue(String string) {
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
    public int horizontalAlignment() {
        return this.horizontalAlignment;
    }
}
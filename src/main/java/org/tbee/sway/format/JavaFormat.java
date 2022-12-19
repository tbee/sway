package org.tbee.sway.format;

import java.text.ParseException;

public class JavaFormat<T> implements Format<T> {

    private final java.text.Format format;

    public JavaFormat(java.text.Format format) {
        this.format = format;
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
}
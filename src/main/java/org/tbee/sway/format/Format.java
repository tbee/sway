package org.tbee.sway.format;

public interface Format<T> {
    String toString(T value);
    T toValue(String string);

    default int columns() {
        return 10;
    }
}
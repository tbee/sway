package org.tbee.sway.format;

import org.tbee.sway.support.HorizontalAlignment;

public interface Format<T> {
    String toString(T value);

    T toValue(String string);

    default int columns() {
        return 20;
    }

    default HorizontalAlignment horizontalAlignment() {
        return HorizontalAlignment.LEADING;
    }
}
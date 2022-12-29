package org.tbee.sway.format;

import org.tbee.sway.support.HAlign;

public interface Format<T> {
    String toString(T value);

    T toValue(String string);

    default int columns() {
        return 20;
    }

    default HAlign horizontalAlignment() {
        return HAlign.LEADING;
    }
}
package org.tbee.sway.format;

import javax.swing.SwingUtilities;

public interface Format<T> {
    String toString(T value);

    T toValue(String string);

    default int columns() {
        return 20;
    }

    default int horizontalAlignment() {
        return SwingUtilities.LEADING;
    }
}
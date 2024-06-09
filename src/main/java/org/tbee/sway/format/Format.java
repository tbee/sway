package org.tbee.sway.format;

import org.tbee.sway.support.HAlign;

import javax.swing.Icon;
import java.awt.Component;
import java.util.function.Consumer;

public interface Format<T> {
    String toString(T value);

    T toValue(String string);

    default int columns() {
        return -1;
    }

    default HAlign horizontalAlignment() {
        return HAlign.LEADING;
    }
    default Icon toIcon(T value) {
        return null;
    }

    interface Editor<T> {
        void open(Component owner, T value, Consumer<T> callback);
    }
    default Editor<T> editor() {
        return null;
    }
}
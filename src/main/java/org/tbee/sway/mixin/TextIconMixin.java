package org.tbee.sway.mixin;

import javax.swing.Icon;

public interface TextIconMixin<T> {

    void setIcon(Icon v);
    void setText(String text);

    default T icon(Icon v) {
        setIcon(v);
        return (T)this;
    }

    default T text(String v) {
        setText(v);
        return (T)this;
    }
}

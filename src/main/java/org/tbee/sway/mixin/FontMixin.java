package org.tbee.sway.mixin;

import java.awt.Font;

public interface FontMixin<T> {

    void setFont(Font f);

    default T font(Font font) {
        setFont(font);
        return (T)this;
    }
}

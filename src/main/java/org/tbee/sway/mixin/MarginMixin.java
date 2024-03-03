package org.tbee.sway.mixin;

import java.awt.Insets;

public interface MarginMixin<T> {

    void setMargin(Insets m);
    default T margin(Insets m) {
        setMargin(m);
        return (T)this;
    }
    default T margin(int top, int left, int bottom, int right) {
        setMargin(new Insets(top, left, bottom, right));
        return (T)this;
    }
}

package org.tbee.sway.mixin;

public interface ToolTipMixin<T> {

    void setToolTipText(String text);

    default T toolTipText(String text) {
        setToolTipText(text);
        return (T)this;
    }
}

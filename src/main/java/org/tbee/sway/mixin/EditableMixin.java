package org.tbee.sway.mixin;

public interface EditableMixin<T> {

    void setEditable(boolean b);
    default T editable(boolean enabled) {
        setEditable(enabled);
        return (T)this;
    }
}

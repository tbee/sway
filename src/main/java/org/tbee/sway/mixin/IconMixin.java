package org.tbee.sway.mixin;

import org.tbee.sway.binding.BindingEndpoint;

import javax.swing.Icon;

public interface IconMixin<T> {

    void setIcon(Icon v);
    default T icon(Icon v) {
        setIcon(v);
        return (T)this;
    }
    String ICON = "icon";
    default BindingEndpoint<String> icon$() {
        return BindingEndpoint.of(this, ICON);
    }
}

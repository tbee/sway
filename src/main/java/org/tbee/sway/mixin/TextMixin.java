package org.tbee.sway.mixin;

import org.tbee.sway.binding.BindingEndpoint;

import javax.swing.Icon;

public interface TextMixin<T> {

    void setText(String text);
    default T text(String v) {
        setText(v);
        return (T)this;
    }
    String TEXT = "text";
    default BindingEndpoint<String> text$() {
        return BindingEndpoint.of(this, TEXT);
    }
}

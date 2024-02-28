package org.tbee.sway.mixin;

import org.tbee.sway.SCheckBox;
import org.tbee.sway.binding.BindingEndpoint;
import org.tbee.sway.binding.ExceptionHandler;

import java.awt.Font;

public interface SelectedMixin<T> {

    ExceptionHandler getExceptionHandler();

    void setSelected(boolean b);
    String SELECTED = "selected";
    default T selected(boolean value) {
        setSelected(value);
        return (T)this;
    }
    default BindingEndpoint<Boolean> selected$() {
        return BindingEndpoint.of(this, SELECTED, getExceptionHandler());
    }
}

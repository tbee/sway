package org.tbee.sway.mixin;

import org.tbee.sway.binding.BindingEndpoint;
import org.tbee.sway.binding.ExceptionHandler;

import java.util.List;

public interface SelectionMixin<T, V> {

    ExceptionHandler getExceptionHandler();

    void setSelection(List<V> values);
    String SELECTION = "selection";
    default T selection(List<V> value) {
        setSelection(value);
        return (T)this;
    }
    default BindingEndpoint<List<V>> selection$() {
        return BindingEndpoint.of(this, SELECTION, getExceptionHandler());
    }
}

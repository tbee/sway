package org.tbee.sway.mixin;

import org.tbee.sway.binding.BindingEndpoint;

import java.util.List;

public interface DataMixin<T, V> extends ExceptionHandlerMixin<T> {

    void setData(List<V> value);
    String DATA = "data";
    default T data(List<V> v) {
        setData(v);
        return (T)this;
    }
    default BindingEndpoint<List<V>> data$() {
        return BindingEndpoint.of(this, DATA, getExceptionHandler());
    }
}

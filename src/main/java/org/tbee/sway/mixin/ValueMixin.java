package org.tbee.sway.mixin;

import org.tbee.sway.binding.BindingEndpoint;

public interface ValueMixin<T, V> extends ExceptionHandlerMixin<T> {

    void setValue(V value);
    String VALUE = "value";
    default T value(V v) {
        setValue(v);
        return (T)this;
    }
    default BindingEndpoint<V> value$() {
        return BindingEndpoint.of(this, VALUE, getExceptionHandler());
    }
}

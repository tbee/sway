package org.tbee.sway.mixin;

import org.tbee.sway.binding.BindingEndpoint;

import java.util.List;

public interface ItemsMixin<T, V> extends ExceptionHandlerMixin<T> {

    List<V> getItems();
    void setItems(List<V> value);
    String ITEMS = "items";
    default T items(List<V> v) {
        setItems(v);
        return (T)this;
    }
    default BindingEndpoint<List<V>> items$() {
        return BindingEndpoint.of(this, ITEMS, getExceptionHandler());
    }
}

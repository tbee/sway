package org.tbee.sway.mixin;

import org.tbee.sway.binding.BeanBinder;
import org.tbee.sway.binding.BindingEndpoint;

public interface BindToMixin<T, V> {

    BindingEndpoint<V> defaultBindingEndpoint();

    /**
     * Binds the default property
     */
    default T bindTo(BindingEndpoint<V> bindingEndpoint) {
        defaultBindingEndpoint().bindTo(bindingEndpoint);
        return (T)this;
    }

    /**
     * Binds to the default property.
     * Binding in this way is not type safe!
     */
    default T bindTo(Object bean, String propertyName) {
        return bindTo(BindingEndpoint.of(bean, propertyName));
    }

    /**
     * Binds to the default property.
     * Binding in this way is not type safe!
     */
    default T bindTo(BeanBinder<?> beanBinder, String propertyName) {
        return bindTo(BindingEndpoint.of(beanBinder, propertyName));
    }

}

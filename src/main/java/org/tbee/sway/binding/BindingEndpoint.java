package org.tbee.sway.binding;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class BindingEndpoint<PropertyClass> {
    private final Object bean;
    private final BeanBinder<?> beanBinder;
    private final String propertyName;
    private final ExceptionHandler exceptionHandler;

    public BindingEndpoint(Object bean, BeanBinder<?> beanBinder, String propertyName, ExceptionHandler exceptionHandler) {
        this.bean = bean;
        this.beanBinder = beanBinder;
        this.propertyName = propertyName;
        this.exceptionHandler = exceptionHandler;
    }

    public Object bean() {
        return bean;
    }

    public BeanBinder<?> beanBinder() {
        return beanBinder;
    }

    public String propertyName() {
        return propertyName;
    }

    public ExceptionHandler exceptionHandler() {
        return exceptionHandler;
    }

    // ==================================================
    // OF

    static public <PropertyClass> BindingEndpoint<PropertyClass> of(Object bean, String propertyName, ExceptionHandler exceptionHandler) {
        return new BindingEndpoint<>(bean, null, propertyName, exceptionHandler);
    }
    static public <PropertyClass> BindingEndpoint<PropertyClass> of(Object bean, String propertyName) {
        return new BindingEndpoint<>(bean, null, propertyName, null);
    }
    static public <PropertyClass> BindingEndpoint<PropertyClass> of(BeanBinder<?> beanBinder, String propertyName, ExceptionHandler exceptionHandler) {
        return new BindingEndpoint<>(null, beanBinder, propertyName, exceptionHandler);
    }
    static public <PropertyClass> BindingEndpoint<PropertyClass> of(BeanBinder<?> beanBinder, String propertyName) {
        return new BindingEndpoint<>(null, beanBinder, propertyName, null);
    }

    // ==================================================
    // BIND

    public Binding bindTo(BindingEndpoint<PropertyClass> bindingEndpoint) {
        if (bindingEndpoint.beanBinder != null) {
            return BindUtil.bind(this.bean, this.propertyName, bindingEndpoint.beanBinder, bindingEndpoint.propertyName, exceptionHandler != null ? exceptionHandler : bindingEndpoint.exceptionHandler);
        }
        return BindUtil.bind(this.bean, this.propertyName, bindingEndpoint.bean, bindingEndpoint.propertyName, exceptionHandler != null? exceptionHandler : bindingEndpoint.exceptionHandler);
    }

    public Binding bindTo(BeanBinder<?> beanBinder, String propertyName) {
        return bindTo(BindingEndpoint.of(beanBinder, propertyName));
    }

    // ==================================================
    // change

    /**
     * Listen for changes and get informed with the old and new value
     * @param biconsumer
     * @return
     * @param <T>
     */
    public <T> BindingEndpoint<PropertyClass> onChange(BiConsumer<T, T> biconsumer) {
        if (this.beanBinder != null) {
            BindUtil.onChange(this.beanBinder, this.propertyName, biconsumer);
        }
        else {
            BindUtil.onChange(this.bean, this.propertyName, biconsumer);
        }
        return this;
    }

    /**
     * Listen for changes and get informed with the new value
     * @param consumer
     * @return
     * @param <T>
     */
    public <T> BindingEndpoint<PropertyClass> onChange(Consumer<T> consumer) {
        if (this.beanBinder != null) {
            BindUtil.onChange(this.beanBinder, this.propertyName, consumer);
        }
        else {
            BindUtil.onChange(this.bean, this.propertyName, consumer);
        }
        return this;
    }


// TBEERNOT need to think about this a bit more
//    // ==================================================
//    // CHAIN
//    final private List<BindChainNode> chain = new ArrayList<>();
//
//    public BindingEndpoint<PropertyClass> and(BindChainNode<?,?> bindChainNode) {
//        chain.add(bindChainNode);
//        return this;
//    }
//    public BindingEndpoint<PropertyClass> add(int v) {
//        return and(Add.of(v));
//    }

}

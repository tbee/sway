package org.tbee.sway.binding;

import java.util.ArrayList;
import java.util.List;

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

    static public <T> BindingEndpoint of(Object bean, String propertyName, ExceptionHandler exceptionHandler) {
        return new BindingEndpoint(bean, null, propertyName, exceptionHandler);
    }
    static public <T> BindingEndpoint of(Object bean, String propertyName) {
        return new BindingEndpoint(bean, null, propertyName, null);
    }
    static public <T> BindingEndpoint of(BeanBinder<?> beanBinder, String propertyName, ExceptionHandler exceptionHandler) {
        return new BindingEndpoint(null, beanBinder, propertyName, exceptionHandler);
    }
    static public <T> BindingEndpoint of(BeanBinder<?> beanBinder, String propertyName) {
        return new BindingEndpoint(null, beanBinder, propertyName, null);
    }

    public Binding bind(BindingEndpoint bindingEndpoint) {
        if (bindingEndpoint.beanBinder != null) {
            return BindUtil.bind(this.bean, this.propertyName, bindingEndpoint.beanBinder, bindingEndpoint.propertyName, exceptionHandler != null ? exceptionHandler : bindingEndpoint.exceptionHandler, chain);
        }
        return BindUtil.bind(this.bean, this.propertyName, bindingEndpoint.bean, bindingEndpoint.propertyName, exceptionHandler != null? exceptionHandler : bindingEndpoint.exceptionHandler, chain);
    }

    public BindingEndpoint<PropertyClass> and(BindChainNode<?,?> bindChainNode) {
        chain.add(bindChainNode);
        return this;
    }
    final private List<BindChainNode> chain = new ArrayList<>();
    public BindingEndpoint<PropertyClass> add(int v) {
        return and(Add.of(v));
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
}

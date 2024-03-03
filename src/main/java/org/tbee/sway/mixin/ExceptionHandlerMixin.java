package org.tbee.sway.mixin;

import org.tbee.sway.binding.BindingEndpoint;
import org.tbee.sway.binding.ExceptionHandler;

import javax.swing.JComponent;

public interface ExceptionHandlerMixin<T> {

    void setExceptionHandler(ExceptionHandler v);
    ExceptionHandler getExceptionHandler();

    default T exceptionHandler(ExceptionHandler v) {
        setExceptionHandler(v);
        return (T)this;
    }
    String EXCEPTIONHANDLER = "exceptionHandler";
    default BindingEndpoint<ExceptionHandler> exceptionHandler$() {
        return BindingEndpoint.of(this, EXCEPTIONHANDLER, getExceptionHandler());
    }

    boolean handleException(Throwable e);
    default boolean handleException(Throwable e, JComponent component, Object oldValue, Object newValue) {
        return handleException(e);
    }
}

package org.tbee.sway.mixin;

import org.tbee.sway.binding.BindingEndpoint;
import org.tbee.sway.binding.ExceptionHandler;

public interface ExceptionHandlerMixin<T> extends ExceptionHandleMixin<T> {

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
}

package org.tbee.sway.binding;

@FunctionalInterface
public interface ExceptionHandler {
    boolean handle(Throwable t, Object oldValue, Object newValue);
}

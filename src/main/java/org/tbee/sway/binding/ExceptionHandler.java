package org.tbee.sway.binding;

import javax.swing.JComponent;

@FunctionalInterface
public interface ExceptionHandler {
    boolean handle(Throwable t, JComponent component, Object oldValue, Object newValue);
}

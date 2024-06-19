package org.tbee.sway.mixin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.Component;

public interface ExceptionHandleMixin<T> {

    default boolean handleException(Throwable e) {

        // Force focus back
        if (this instanceof JComponent jComponent) {
            SwingUtilities.invokeLater(() -> jComponent.grabFocus());
        }

        // Display the error
        Logger logger = LoggerFactory.getLogger(this.getClass());
        if (logger.isInfoEnabled()) logger.info(e.getMessage(), e);
        if (this instanceof Component component) {
            JOptionPane.showMessageDialog(component, e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }

        // Mark exception as handled
        return true;
    }
    default boolean handleException(Throwable e, JComponent component, Object oldValue, Object newValue) {
        return handleException(e);
    }
}

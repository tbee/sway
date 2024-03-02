package org.tbee.sway.mixin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tbee.sway.binding.BindingEndpoint;
import org.tbee.sway.binding.ExceptionHandler;
import org.tbee.util.ExceptionUtil;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public interface ExceptionHandlerMixin<T extends JComponent> {

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


    default boolean handleException(Throwable e, JComponent component, Object oldValue, Object newValue) {
        return handleException(e);
    }
    default boolean handleException(Throwable e) {

        // Force focus back
        SwingUtilities.invokeLater(() -> ((T)this).grabFocus());

        // Display the error
        Logger logger = LoggerFactory.getLogger(this.getClass());
        if (logger.isDebugEnabled()) logger.debug(e.getMessage(), e);
        JOptionPane.showMessageDialog(((T)this), ExceptionUtil.determineMessage(e), "ERROR", JOptionPane.ERROR_MESSAGE);

        // Mark exception as handled
        return true;
    }
}

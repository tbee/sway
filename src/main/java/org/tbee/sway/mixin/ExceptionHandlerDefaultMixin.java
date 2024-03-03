package org.tbee.sway.mixin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public interface ExceptionHandlerDefaultMixin<T extends JComponent> extends ExceptionHandlerMixin {

    default boolean handleException(Throwable e) {

        // Force focus back
        SwingUtilities.invokeLater(() -> ((T)this).grabFocus());

        // Display the error
        Logger logger = LoggerFactory.getLogger(this.getClass());
        if (logger.isDebugEnabled()) logger.debug(e.getMessage(), e);
        JOptionPane.showMessageDialog(((T)this), e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);

        // Mark exception as handled
        return true;
    }
}

package org.tbee.sway;

import org.tbee.sway.binding.ExceptionHandler;
import org.tbee.sway.mixin.ActionMixin;
import org.tbee.sway.mixin.ComponentMixin;
import org.tbee.sway.mixin.ExceptionHandlerMixin;
import org.tbee.sway.mixin.HAlignMixin;
import org.tbee.sway.mixin.TextIconMixin;
import org.tbee.sway.mixin.ToolTipMixin;
import org.tbee.sway.mixin.VAlignMixin;
import org.tbee.util.ExceptionUtil;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SButton extends JButton implements
        HAlignMixin<SButton>,
        VAlignMixin<SButton>,
        ExceptionHandlerMixin<SButton>,
        ToolTipMixin<SButton>,
        ActionMixin<SButton>,
        TextIconMixin<SButton>,
        ComponentMixin<SButton> {
    final static private org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SButton.class);

    public SButton() {
    }

    public SButton(Icon icon) {
        super(icon);
    }

    public SButton(String text) {
        super(text);
    }

    public SButton(Action a) {
        super(a);
    }

    public SButton(String text, Icon icon) {
        super(text, icon);
    }

    // ===========================================================================================================================
    // For Mixins

    @Override
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        super.firePropertyChange(propertyName, oldValue, newValue);
    }

    // ==============================================
    // ExceptionHandler
    
    /**
     * Set the ExceptionHandler used a.o. when the actionListeners are called.
     * @param v
     */
    public void setExceptionHandler(ExceptionHandler v) {
        firePropertyChange(EXCEPTIONHANDLER, exceptionHandler, exceptionHandler = v);
    }

    public ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }
    private ExceptionHandler exceptionHandler = this::handleException;

    private boolean handleException(Throwable e, JComponent component, Object oldValue, Object newValue) {
    	
        if (LOGGER.isDebugEnabled()) LOGGER.debug(e.getMessage(), e);
        JOptionPane.showMessageDialog(this, ExceptionUtil.determineMessage(e), "ERROR", JOptionPane.ERROR_MESSAGE);

        // Mark exception as handled
        return true;
    }

    @Override
    protected void fireActionPerformed(ActionEvent event) {
    	try {
    		super.fireActionPerformed(event);
    	}
    	catch (Throwable t) {
    		if (exceptionHandler != null && exceptionHandler.handle(t, this, null, null)) {
    			return;
    		}
			throw t;
    	}
    }
    
    // ==============================================
    // FLUENT API

    static public SButton of() {
        return new SButton();
    }

    static public SButton of(Icon icon) {
        return of().icon(icon);
    }

    static public SButton of(String text) {
        return of().text(text);
    }

    static public SButton of(String text, Icon icon) {
        return of().text(text).icon(icon);
    }

    static public SButton of(Action a) {
        return of().action(a);
    }

    static public SButton of(Icon icon, ActionListener actionListener) {
        return of().icon(icon).onAction(actionListener);
    }

    static public SButton of(String text, ActionListener actionListener) {
        return of().text(text).onAction(actionListener);
    }

    static public SButton of(String text, Icon icon, ActionListener actionListener) {
        return of().text(text).icon(icon).onAction(actionListener);
    }

    public SButton margin(Insets m) {
        setMargin(m);
        return this;
    }
}

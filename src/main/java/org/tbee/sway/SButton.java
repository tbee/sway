package org.tbee.sway;

import org.tbee.sway.binding.BindingEndpoint;
import org.tbee.sway.binding.ExceptionHandler;
import org.tbee.sway.support.HAlign;
import org.tbee.sway.support.VAlign;
import org.tbee.util.ExceptionUtil;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;

public class SButton extends JButton {
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

    // ==============================================
    // JavaBean

    /**
     * Enum variant of HorizontalAlignment
     * @param v
     */
    public void setHAlign(HAlign v) {
        HAlign old = getHAlign();
        setHorizontalAlignment(v.getSwingConstant());
        firePropertyChange(HALIGN, old, v);
    }
    public HAlign getHAlign() {
        return HAlign.of(getHorizontalAlignment());
    }
    public SButton hAlign(HAlign v) {
        setHAlign(v);
        return this;
    }
    final static public String HALIGN = "hAlign";
    public BindingEndpoint<HAlign> hAlign$() {
        return BindingEndpoint.of(this, HALIGN, exceptionHandler);
    }

    /**
     * Enum variant of VerticalAlignment
     * @param v
     */
    public void setVAlign(VAlign v) {
        VAlign old = getVAlign();
        setVerticalAlignment(v.getSwingConstant());
        firePropertyChange(VALIGN, old, v);
    }
    public VAlign getVAlign() {
        return VAlign.of(getHorizontalAlignment());
    }
    public SButton vAlign(VAlign v) {
        setVAlign(v);
        return this;
    }
    final static public String VALIGN = "vAlign";
    public BindingEndpoint<VAlign> vAlign$() {
        return BindingEndpoint.of(this, VALIGN, exceptionHandler);
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
    public SButton exceptionHandler(ExceptionHandler v) {
        setExceptionHandler(v);
        return this;
    }
    private ExceptionHandler exceptionHandler = this::handleException;
    final static public String EXCEPTIONHANDLER = "exceptionHandler";
    public BindingEndpoint<ExceptionHandler> exceptionHandler$() {
        return BindingEndpoint.of(this, EXCEPTIONHANDLER, exceptionHandler);
    }

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

    public SButton name(String v) {
        setName(v);
        return this;
    }

    public SButton toolTipText(String t) {
        setToolTipText(t);
        return this;
    }

    public SButton enabled(boolean v) {
        setEnabled(v);
        return this;
    }

    public SButton margin(Insets m) {
        setMargin(m);
        return this;
    }

    public SButton onAction(ActionListener l) {
        addActionListener(l);
        return this;
    }

    public SButton action(Action v) {
        setAction(v);
        return this;
    }

    public SButton icon(Icon v) {
        setIcon(v);
        return this;
    }

    public SButton text(String v) {
        setText(v);
        return this;
    }

    public SButton visible(boolean value) {
        setVisible(value);
        return this;
    }

    public SButton withPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        super.addPropertyChangeListener(propertyName, listener);
        return this;
    }
    public SButton withPropertyChangeListener(PropertyChangeListener listener) {
        super.addPropertyChangeListener(listener);
        return this;
    }

    public SButton overlayWith(Component overlayComponent) {
        SOverlayPane.overlayWith(this, overlayComponent);
        return this;
    }
    public SButton removeOverlay(Component overlayComponent) {
        SOverlayPane.removeOverlay(this, overlayComponent);
        return this;
    }
}

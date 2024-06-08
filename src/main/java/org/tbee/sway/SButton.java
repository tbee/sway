package org.tbee.sway;

import org.tbee.sway.binding.ExceptionHandler;
import org.tbee.sway.mixin.ActionMixin;
import org.tbee.sway.mixin.ExceptionHandlerDefaultMixin;
import org.tbee.sway.mixin.HAlignMixin;
import org.tbee.sway.mixin.JComponentMixin;
import org.tbee.sway.mixin.TextIconMixin;
import org.tbee.sway.mixin.ToolTipMixin;
import org.tbee.sway.mixin.VAlignMixin;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SButton extends JButton implements
        HAlignMixin<SButton>,
        VAlignMixin<SButton>,
        ExceptionHandlerDefaultMixin<SButton>,
        ToolTipMixin<SButton>,
        ActionMixin<SButton>,
        TextIconMixin<SButton>,
        JComponentMixin<SButton> {
    final static private org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SButton.class);

    public static final Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);
    public static final Cursor CLICKABLE_CURSOR = new Cursor(Cursor.HAND_CURSOR);

    public SButton() { }

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

    public SButton transparentAsLabel() {
        setOpaque(false);
        setContentAreaFilled(false);

        // TODO: make sure focus is still visible: how to get the current focused border?
//        setBorderPainted(false);
//        onFocusGained(e -> border(BorderFactory.createLineBorder(Color.RED, 1)));
//        onFocusLost(e -> border(BorderFactory.createEmptyBorder(1,1,1,1)));
        return this;
    }

    public SButton showMouseOverInCursor() {
        onMouseEnter(e -> setCursor(CLICKABLE_CURSOR));
        onMouseExit(e -> setCursor(DEFAULT_CURSOR));
        return this;
    }

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
}

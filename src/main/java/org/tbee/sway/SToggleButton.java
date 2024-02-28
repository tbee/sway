package org.tbee.sway;

import org.tbee.sway.binding.ExceptionHandler;
import org.tbee.sway.mixin.ActionMixin;
import org.tbee.sway.mixin.ComponentMixin;
import org.tbee.sway.mixin.ExceptionHandlerMixin;
import org.tbee.sway.mixin.SelectedMixin;
import org.tbee.sway.mixin.TextIconMixin;
import org.tbee.sway.mixin.ToolTipMixin;
import org.tbee.util.ExceptionUtil;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import java.awt.event.ActionListener;

// TODO:
// - binding (2/3? values)

/**
 *
 */
public class SToggleButton extends JToggleButton implements
        ComponentMixin<SToggleButton>,
        TextIconMixin<SToggleButton>,
        ActionMixin<SToggleButton>,
        ExceptionHandlerMixin<SToggleButton>,
        SelectedMixin<SToggleButton>,
        ToolTipMixin<SToggleButton> {
    static private org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SToggleButton.class);

    public SToggleButton() {
    }

    public SToggleButton(Icon icon) {
        super(icon);
    }

    public SToggleButton(Icon icon, boolean selected) {
        super(icon, selected);
    }

    public SToggleButton(String text) {
        super(text);
    }

    public SToggleButton(String text, boolean selected) {
        super(text, selected);
    }

    public SToggleButton(Action a) {
        super(a);
    }

    public SToggleButton(String text, Icon icon) {
        super(text, icon);
    }

    public SToggleButton(String text, Icon icon, boolean selected) {
        super(text, icon, selected);
    }

    // ===========================================================================================================================
    // For Mixins

    // ========================================================
    // EXCEPTION HANDLER

    /**
     * Set the ExceptionHandler used a.o. in binding
     * @param v
     */
    public void setExceptionHandler(ExceptionHandler v) {
        firePropertyChange(EXCEPTIONHANDLER, exceptionHandler, exceptionHandler = v);
    }
    public ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }
    ExceptionHandler exceptionHandler = this::handleException;

    private boolean handleException(Throwable e, JComponent component, Object oldValue, Object newValue) {
        return handleException(e);
    }
    private boolean handleException(Throwable e) {

        // Force focus back
        SwingUtilities.invokeLater(() -> this.grabFocus());

        // Display the error
        if (LOGGER.isDebugEnabled()) LOGGER.debug(e.getMessage(), e);
        JOptionPane.showMessageDialog(this, ExceptionUtil.determineMessage(e), "ERROR", JOptionPane.ERROR_MESSAGE);

        // Mark exception as handled
        return true;
    }

    // ===========================================================================================================================
    // FLUENT API

    static public SToggleButton of() {
        return new SToggleButton();
    }

    static public SToggleButton of(Icon icon) {
        return of().icon(icon);
    }

    static public SToggleButton of(Icon icon, boolean selected) {
        return of().icon(icon).selected(selected);
    }

    static public SToggleButton of(String text) {
        return of().text(text);
    }

    static public SToggleButton of(String text, boolean selected) {
        return of().text(text).selected(selected);
    }

    static public SToggleButton of(String text, Icon icon) {
        return of().text(text).icon(icon);
    }

    static public SToggleButton of(String text, Icon icon, boolean selected) {
        return of().text(text).icon(icon).selected(selected);
    }

    static public SToggleButton of(Action a) {
        return of().action(a);
    }

    static public SToggleButton of(Icon icon, ActionListener actionListener) {
        return of().icon(icon).onAction(actionListener);
    }

    static public SToggleButton of(Icon icon, boolean selected, ActionListener actionListener) {
        return of().icon(icon).selected(selected).onAction(actionListener);
    }

    static public SToggleButton of(String text, ActionListener actionListener) {
        return of().text(text).onAction(actionListener);
    }

    static public SToggleButton of(String text, boolean selected, ActionListener actionListener) {
        return of().text(text).selected(selected).onAction(actionListener);
    }

    static public SToggleButton of(String text, Icon icon, ActionListener actionListener) {
        return of().text(text).icon(icon).onAction(actionListener);
    }
    static public SToggleButton of(String text, Icon icon, boolean selected, ActionListener actionListener) {
        return of().text(text).icon(icon).selected(selected).onAction(actionListener);
    }
}

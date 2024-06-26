package org.tbee.sway;

import org.tbee.sway.binding.BindingEndpoint;
import org.tbee.sway.binding.ExceptionHandler;
import org.tbee.sway.mixin.HAlignMixin;
import org.tbee.sway.mixin.JComponentMixin;
import org.tbee.sway.mixin.OverlayMixin;
import org.tbee.sway.mixin.TextIconMixin;
import org.tbee.sway.mixin.ToolTipMixin;
import org.tbee.sway.mixin.VAlignMixin;
import org.tbee.sway.support.HAlign;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

// TODO:

/**
 *
 */
public class SLabel extends JLabel implements
        HAlignMixin<SLabel>,
        VAlignMixin<SLabel>,
        OverlayMixin<SLabel>,
        JComponentMixin<SLabel>,
        TextIconMixin<SLabel>,
        ToolTipMixin<SLabel> {

    public SLabel() {
    }

    public SLabel(String text, Icon icon) {
        super(text, icon, SwingConstants.CENTER);
    }

    public SLabel(String text) {
        super(text);
    }

    public SLabel(Icon icon) {
        super(icon);
    }


    // ===========================================================================================================================
    // For Mixins

    @Override
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        super.firePropertyChange(propertyName, oldValue, newValue);
    }

    @Override
    public ExceptionHandler getExceptionHandler() {
        return null;
    }

    // ===========================================================================================================================
    // JavaBean

    /**
     * Add PCE event
     */
    public void setVisible(boolean v) {
        boolean old = super.isVisible();
        super.setVisible(v);
        firePropertyChange(VISIBLE, old, v);
    }

    /**
     * Add PCE event
     */
    public void setText(String v) {
        String old = super.getText();
        super.setText(v);
        firePropertyChange(TEXT, old, v);
    }

    /**
     * Add PCE event
     */
    public void setIcon(Icon v) {
        Icon old = super.getIcon();
        super.setIcon(v);
        firePropertyChange(ICON, old, v);
    }

    // ===========================================================================================================================
    // FLUENT API

    static public SLabel of() {
        return new SLabel();
    }
    static public SLabel of(String text, Icon icon) {
        return of().text(text).icon(icon).hAlign(HAlign.CENTER);
    }

    static public SLabel of(String text) {
        return of().text(text);
    }

    static public SLabel of(Icon icon) {
        return of().icon(icon);
    }
}

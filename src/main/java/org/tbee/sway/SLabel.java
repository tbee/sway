package org.tbee.sway;

import org.tbee.sway.binding.BindingEndpoint;
import org.tbee.sway.binding.ExceptionHandler;
import org.tbee.sway.mixin.ComponentMixin;
import org.tbee.sway.mixin.HAlignMixin;
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
        ComponentMixin<SLabel>,
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
    final static public String VISIBLE = "visible";
    public BindingEndpoint<Boolean> visible$() {
        return BindingEndpoint.of(this, VISIBLE);
    }

    /**
     * Add PCE event
     */
    public void setText(String v) {
        String old = super.getText();
        super.setText(v);
        firePropertyChange(TEXT, old, v);
    }
    final static public String TEXT = "text";
    public BindingEndpoint<String> text$() {
        return BindingEndpoint.of(this, TEXT);
    }

    /**
     * Add PCE event
     */
    public void setIcon(Icon v) {
        Icon old = super.getIcon();
        super.setIcon(v);
        firePropertyChange(TEXT, old, v);
    }
    final static public String ICON = "icon";
    public BindingEndpoint<Icon> ioon$() {
        return BindingEndpoint.of(this, ICON);
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

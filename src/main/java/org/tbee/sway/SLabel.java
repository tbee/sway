package org.tbee.sway;

import org.tbee.sway.binding.BindingEndpoint;
import org.tbee.sway.support.HAlign;
import org.tbee.sway.support.VAlign;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.beans.PropertyChangeListener;

// TODO:

/**
 *
 */
public class SLabel extends JLabel {

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
    public SLabel hAlign(HAlign v) {
        setHAlign(v);
        return this;
    }
    final static public String HALIGN = "hAlign";
    public BindingEndpoint<HAlign> hAlign$() {
        return BindingEndpoint.of(this, HALIGN);
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
    public SLabel vAlign(VAlign v) {
        setVAlign(v);
        return this;
    }
    final static public String VALIGN = "vAlign";
    public BindingEndpoint<VAlign> vAlign$() {
        return BindingEndpoint.of(this, VALIGN);
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

    public SLabel name(String v) {
        setName(v);
        return this;
    }

    public SLabel text(String value) {
        setText(value);
        return this;
    }

    public SLabel icon(Icon value) {
        setIcon(value);
        return this;
    }

    public SLabel font(Font value) {
        setFont(value);
        return this;
    }

    public SLabel visible(boolean value) {
        setVisible(value);
        return this;
    }

    public SLabel foreground(Color value) {
        setForeground(value);
        return this;
    }

    public SLabel toolTipText(String text) {
        setToolTipText(text);
        return this;
    }

    public SLabel withPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        super.addPropertyChangeListener(propertyName, listener);
        return this;
    }
    public SLabel withPropertyChangeListener(PropertyChangeListener listener) {
        super.addPropertyChangeListener(listener);
        return this;
    }

    public SLabel overlayWith(Component overlayComponent) {
        SOverlayPane.overlayWith(this, overlayComponent);
        return this;
    }
    public SLabel removeOverlay(Component overlayComponent) {
        SOverlayPane.removeOverlay(this, overlayComponent);
        return this;
    }
}

package org.tbee.sway;

import org.tbee.sway.support.HAlign;
import org.tbee.sway.support.VAlign;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Font;

public class SLabel extends JLabel {

    public SLabel() {
    }

    public SLabel(String text, Icon icon) {
        super(text, icon, SwingConstants.CENTER);
    }

    public SLabel(String text) {
        super(text);
    }

    public SLabel(Icon image) {
        super(image);
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

    /**
     * Add PCE event
     */
    public void setText(String v) {
        String old = super.getText();
        super.setText(v);
        firePropertyChange(TEXT, old, v);
    }
    final static public String TEXT = "text";

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
    public SLabel VAlign(VAlign v) {
        setVAlign(v);
        return this;
    }
    final static public String VALIGN = "vAlign";

    // ===========================================================================================================================
    // FLUENT API

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

    public SLabel text(String value) {
        setText(value);
        return this;
    }

    public SLabel foreground(Color value) {
        setForeground(value);
        return this;
    }
}

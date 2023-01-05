package org.tbee.sway;

import org.tbee.sway.support.HAlign;
import org.tbee.sway.support.VAlign;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import java.awt.Insets;
import java.awt.event.ActionListener;

public class SButton extends JButton {

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
    // JavaBaan

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
    public SButton VAlign(VAlign v) {
        setVAlign(v);
        return this;
    }
    final static public String VALIGN = "vAlign";

    // ==============================================
    // FLUENT API

    public SButton name(String v) {
        setName(v);
        return this;
    }

    public SButton toolTipText(String t) {
        super.setToolTipText(t);
        return this;
    }

    public SButton enabled(boolean v) {
        super.setEnabled(v);
        return this;
    }

    public SButton margin(Insets m) {
        super.setMargin(m);
        return this;
    }

    public SButton onAction(ActionListener l) {
        super.addActionListener(l);
        return this;
    }

    public SButton action(Action v) {
        super.setAction(v);
        return this;
    }

    public SButton icon(Icon v) {
        super.setIcon(v);
        return this;
    }

    public SButton text(String v) {
        super.setText(v);
        return this;
    }

    public SButton visible(boolean value) {
        setVisible(value);
        return this;
    }
}

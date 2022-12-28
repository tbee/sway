package org.tbee.sway;

import org.tbee.sway.support.HorizontalAlignment;
import org.tbee.sway.support.VerticalAlignment;

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
    // FLUENT API

    /** */
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

    public SButton actionListener(ActionListener l) {
        super.addActionListener(l);
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

    public SButton action(Action v) {
        super.setAction(v);
        return this;
    }

    public SButton horizontalAlignment(HorizontalAlignment v) {
        setHorizontalAlignment(v.getSwingConstant());
        return this;
    }

    public SButton verticalAlignment(VerticalAlignment v) {
        setVerticalAlignment(v.getSwingConstant());
        return this;
    }
}

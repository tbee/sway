package org.tbee.sway;

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

    public JButton toolTipText(String t) {
        super.setToolTipText(t);
        return this;
    }

    public JButton enabled(boolean v) {
        super.setEnabled(v);
        return this;
    }

    public JButton margin(Insets m) {
        super.setMargin(m);
        return this;
    }

    public JButton actionListener(ActionListener l) {
        super.addActionListener(l);
        return this;
    }

    public JButton icon(Icon i) {
        super.setIcon(i);
        return this;
    }

    public JButton action(Action v) {
        super.setAction(v);
        return this;
    }
}

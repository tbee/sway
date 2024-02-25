package org.tbee.sway;

import org.tbee.sway.mixin.ComponentMixin;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JToggleButton;
import java.awt.event.ActionListener;

// TODO:
// - binding (2/3? values)

/**
 *
 */
public class SToggleButton extends JToggleButton implements
        ComponentMixin<SToggleButton> {

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
    // FLUENT API

    public SToggleButton text(String value) {
        setText(value);
        return this;
    }

    public SToggleButton icon(Icon value) {
        setIcon(value);
        return this;
    }

    public SToggleButton actionListener(ActionListener l) {
        super.addActionListener(l);
        return this;
    }

    public SToggleButton action(Action v) {
        super.setAction(v);
        return this;
    }

    public SToggleButton toolTipText(String text) {
        setToolTipText(text);
        return this;
    }
}

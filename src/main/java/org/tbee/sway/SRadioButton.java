package org.tbee.sway;

import org.tbee.sway.mixin.ActionMixin;
import org.tbee.sway.mixin.ComponentMixin;
import org.tbee.sway.mixin.TextIconMixin;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JRadioButton;
import java.awt.Insets;

public class SRadioButton extends JRadioButton implements
        ComponentMixin<SRadioButton>,
        TextIconMixin<SRadioButton>,
        ActionMixin<SRadioButton> {

    public SRadioButton() {
    }

    public SRadioButton(Icon icon) {
        super(icon);
    }

    public SRadioButton(Action a) {
        super(a);
    }

    public SRadioButton(Icon icon, boolean selected) {
        super(icon, selected);
    }

    public SRadioButton(String text) {
        super(text);
    }

    public SRadioButton(String text, boolean selected) {
        super(text, selected);
    }

    public SRadioButton(String text, Icon icon) {
        super(text, icon);
    }

    public SRadioButton(String text, Icon icon, boolean selected) {
        super(text, icon, selected);
    }

    // ==============================================
    // FLUENT API

    public SRadioButton toolTipText(String t) {
        super.setToolTipText(t);
        return this;
    }

    public SRadioButton margin(Insets m) {
        super.setMargin(m);
        return this;
    }
}

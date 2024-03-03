package org.tbee.sway;

import org.tbee.sway.mixin.ActionMixin;
import org.tbee.sway.mixin.JComponentMixin;
import org.tbee.sway.mixin.MarginMixin;
import org.tbee.sway.mixin.TextIconMixin;
import org.tbee.sway.mixin.ToolTipMixin;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JRadioButton;

public class SRadioButton extends JRadioButton implements
        JComponentMixin<SRadioButton>,
        TextIconMixin<SRadioButton>,
        ToolTipMixin<SRadioButton>,
        MarginMixin<SRadioButton>,
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
}

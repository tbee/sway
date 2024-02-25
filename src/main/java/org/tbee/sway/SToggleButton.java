package org.tbee.sway;

import org.tbee.sway.mixin.ActionMixin;
import org.tbee.sway.mixin.ComponentMixin;
import org.tbee.sway.mixin.TextIconMixin;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JToggleButton;

// TODO:
// - binding (2/3? values)

/**
 *
 */
public class SToggleButton extends JToggleButton implements
        ComponentMixin<SToggleButton>,
        TextIconMixin<SToggleButton>,
        ActionMixin<SToggleButton> {

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

    public SToggleButton toolTipText(String text) {
        setToolTipText(text);
        return this;
    }
}

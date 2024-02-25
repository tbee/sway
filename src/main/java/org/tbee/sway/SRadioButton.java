package org.tbee.sway;

import org.tbee.sway.mixin.PropertyChangeListenerMixin;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JRadioButton;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;

public class SRadioButton extends JRadioButton implements PropertyChangeListenerMixin<SRadioButton> {
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

    public SRadioButton name(String v) {
        setName(v);
        return this;
    }

    public SRadioButton toolTipText(String t) {
        super.setToolTipText(t);
        return this;
    }

    public SRadioButton enabled(boolean v) {
        super.setEnabled(v);
        return this;
    }

    public SRadioButton margin(Insets m) {
        super.setMargin(m);
        return this;
    }

    public SRadioButton actionListener(ActionListener l) {
        super.addActionListener(l);
        return this;
    }

    public SRadioButton action(Action v) {
        super.setAction(v);
        return this;
    }

    public SRadioButton icon(Icon v) {
        super.setIcon(v);
        return this;
    }

    public SRadioButton text(String v) {
        super.setText(v);
        return this;
    }

    public SRadioButton visible(boolean value) {
        setVisible(value);
        return this;
    }

    public SRadioButton withPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        super.addPropertyChangeListener(propertyName, listener);
        return this;
    }
    public SRadioButton withPropertyChangeListener(PropertyChangeListener listener) {
        super.addPropertyChangeListener(listener);
        return this;
    }

    public SRadioButton overlayWith(Component overlayComponent) {
        SOverlayPane.overlayWith(this, overlayComponent);
        return this;
    }
    public SRadioButton removeOverlay(Component overlayComponent) {
        SOverlayPane.removeOverlay(this, overlayComponent);
        return this;
    }
}

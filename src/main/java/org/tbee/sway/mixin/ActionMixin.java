package org.tbee.sway.mixin;

import javax.swing.Action;
import java.awt.event.ActionListener;

public interface ActionMixin<T> {

    void addActionListener(ActionListener l);
    default T onAction(ActionListener l) {
        addActionListener(l);
        return (T)this;
    }

    void setAction(Action a);
    default T action(Action v) {
        setAction(v);
        return (T)this;
    }

    void setActionCommand(String actionCommand);
    default T actionCommand(String v) {
        setActionCommand(v);
        return (T)this;
    }
}

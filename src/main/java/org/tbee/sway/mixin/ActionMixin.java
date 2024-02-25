package org.tbee.sway.mixin;

import javax.swing.Action;
import java.awt.event.ActionListener;

public interface ActionMixin<T> {

    void addActionListener(ActionListener l);
    void setAction(Action a);

    default T actionListener(ActionListener l) {
        addActionListener(l);
        return (T)this;
    }

    default T action(Action v) {
        setAction(v);
        return (T)this;
    }
}

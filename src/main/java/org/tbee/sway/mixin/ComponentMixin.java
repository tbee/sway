package org.tbee.sway.mixin;

import java.awt.Color;
import java.awt.Component;

public interface ComponentMixin<T extends Component> extends
        PropertyChangeListenerMixin<T>,
        OverlayMixin<T> {

    void setName(String name);
    default T name(String v) {
        setName(v);
        return (T)this;
    }

    void setEnabled(boolean b);
    default T enabled(boolean v) {
        setEnabled(v);
        return (T)this;
    }

    void setVisible(boolean b);
    default T visible(boolean v) {
        setVisible(v);
        return (T)this;
    }

    void setForeground(Color c);
    default T foreground(Color c) {
        setForeground(c);
        return (T)this;
    }

    void setBackground(Color c);
    default T background(Color c) {
        setBackground(c);
        return (T)this;
    }
}

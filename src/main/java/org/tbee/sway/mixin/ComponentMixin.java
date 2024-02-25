package org.tbee.sway.mixin;

import java.awt.Color;
import java.awt.Component;

public interface ComponentMixin<T extends Component> extends
        PropertyChangeListenerMixin<T>,
        OverlayMixin<T> {

    void setName(String name);
    void setEnabled(boolean b);
    void setVisible(boolean b);
    void setForeground(Color c);
    void setBackground(Color c);

    default T name(String v) {
        setName(v);
        return (T)this;
    }

    default T enabled(boolean v) {
        setEnabled(v);
        return (T)this;
    }

    default T visible(boolean v) {
        setVisible(v);
        return (T)this;
    }

    default T foreground(Color c) {
        setForeground(c);
        return (T)this;
    }

    default T background(Color c) {
        setBackground(c);
        return (T)this;
    }
}

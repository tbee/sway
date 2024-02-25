package org.tbee.sway.mixin;

import java.awt.Component;

public interface ComponentMixin<T extends Component> extends
        PropertyChangeListenerMixin<T>,
        OverlayMixin<T> {

    void setName(String name);
    void setEnabled(boolean b);
    void setVisible(boolean b);

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
}

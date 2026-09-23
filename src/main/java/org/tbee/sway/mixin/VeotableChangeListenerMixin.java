package org.tbee.sway.mixin;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;
import java.util.function.Consumer;

public interface VeotableChangeListenerMixin<T> {

    void addVetoableChangeListener(VetoableChangeListener listener);

    default T onVetoableChangeListener(Consumer<PropertyChangeEvent> consumer) {
        addVetoableChangeListener(consumer::accept);
        return (T)this;
    }
}

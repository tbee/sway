package org.tbee.sway.mixin;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.function.Consumer;

public interface PropertyChangeListenerMixin<T> {

    void addPropertyChangeListener(PropertyChangeListener listener);
    void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

    default T onPropertyChange(Consumer<PropertyChangeEvent> consumer) {
        addPropertyChangeListener(evt -> consumer.accept(evt));
        return (T)this;
    }

    default T onPropertyChange(String name, Consumer<PropertyChangeEvent> consumer) {
        addPropertyChangeListener(name, evt -> consumer.accept(evt));
        return (T)this;
    }


    default T withPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        addPropertyChangeListener(propertyName, listener);
        return (T)this;
    }
    default T withPropertyChangeListener(PropertyChangeListener listener) {
        addPropertyChangeListener(listener);
        return (T)this;
    }
}

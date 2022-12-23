package org.tbee.util;

import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;

/**
 * The counterpart of the PropertyChangeListener interface.
 * You can use PropertyChangeSupport to easily implement this.
 */
public interface PropertyChangeProvider {

    PropertyChangeListener[] getPropertyChangeListeners();
    PropertyChangeListener[] getPropertyChangeListeners(String property);
    void addPropertyChangeListener(PropertyChangeListener listener);
    void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);
    void removePropertyChangeListener(PropertyChangeListener listener);
    void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);
    void firePropertyChange(String name, Object before, Object after);
    void fireIndexedPropertyChange(String name, int index, Object before, Object after);

    VetoableChangeListener[] getVetoableChangeListeners();
    VetoableChangeListener[] getVetoableChangeListeners(String property);
    void addVetoableChangeListener(VetoableChangeListener o);
    void addVetoableChangeListener(String property, VetoableChangeListener o);
    void removeVetoableChangeListener(VetoableChangeListener o);
    void removeVetoableChangeListener(String property, VetoableChangeListener o);
    void fireVetoableChange(String name, Object before, Object after);
}

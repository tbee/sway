package org.tbee.util;

import java.beans.*;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;


/**
 * a class that implements standard features of a JavaBean.
 * The inherting class should implements:
 * - parameterless constructor
 * - serializable
 * ...
 */
abstract public class AbstractBean<T>
implements PropertyChangeProvider, java.io.Serializable {

    // ===============================================================================================
    // PropertyChange

    transient volatile private PropertyChangeSupport propertyChangeSupport = null;

    /**
     * PropertyChange
     */
    synchronized public boolean hasPropertyChangeListeners() {
        return (propertyChangeSupport != null && propertyChangeSupport.getPropertyChangeListeners().length > 0);
    }

    synchronized public boolean hasPropertyChangeListeners(String property) {
        return (propertyChangeSupport != null && propertyChangeSupport.hasListeners(property));
    }

    @Override
    synchronized public PropertyChangeListener[] getPropertyChangeListeners() {
        if (propertyChangeSupport == null) {
            return new PropertyChangeListener[]{};
        }
        return propertyChangeSupport.getPropertyChangeListeners();
    }

    @Override
    synchronized public PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
        if (propertyChangeSupport == null) {
            return new PropertyChangeListener[]{};
        }
        return propertyChangeSupport.getPropertyChangeListeners(propertyName);
    }

    @Override
    synchronized public void addPropertyChangeListener(PropertyChangeListener o) {
        if (propertyChangeSupport == null) {
            propertyChangeSupport = new PropertyChangeSupport(this);
        }
        propertyChangeSupport.addPropertyChangeListener(o);
    }

    @Override
    synchronized public void addPropertyChangeListener(String propertyName, PropertyChangeListener o) {
        if (propertyChangeSupport == null) {
            propertyChangeSupport = new PropertyChangeSupport(this);
        }
        propertyChangeSupport.addPropertyChangeListener(propertyName, o);
    }

    @Override
    synchronized public void removePropertyChangeListener(PropertyChangeListener o) {
        if (propertyChangeSupport == null) {
            return;
        }
        propertyChangeSupport.removePropertyChangeListener(o);
    }

    @Override
    synchronized public void removePropertyChangeListener(String propertyName, PropertyChangeListener o) {
        if (propertyChangeSupport == null) {
            return;
        }
        propertyChangeSupport.removePropertyChangeListener(propertyName, o);
    }

    @Override
    public void firePropertyChange(String name, Object before, Object after) {
        if (propertyChangeSupport == null) {
            return;
        }

        // do not fire if equal
        if (Objects.equals(before, after)) {
            return;
        }

        // do it
        // Does not fire if either value is null: propertyChangeSupport.firePropertyChange(name, before, after);
        propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, name, before, after));
    }

    /**
     * First PCE even if the values are equal
     * @param name
     * @param before
     * @param after
     */
    public void firePropertyChangeAlways(String name, Object before, Object after) {
        if (propertyChangeSupport == null) {
            return;
        }

        // do it
        // Does not fire if either value is null: propertyChangeSupport.firePropertyChange(name, before, after);
        propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, name, before, after));
    }

    public void fireMultiplePropertiesChanged() {
        this.firePropertyChange((String)null, (Object)null, (Object)null);
    }

    @Override
    public void fireIndexedPropertyChange(String name, int index, Object before, Object after) {
        if (propertyChangeSupport == null) {
            return;
        }
        // Does not fire if either value is null: propertyChangeSupport.fireIndexedPropertyChange(name, index, before, after);
        propertyChangeSupport.firePropertyChange(new IndexedPropertyChangeEvent(this, name, before, after, index));
    }

    /**
     * Call PCE but prefix with possible derived properties.
     * Example:
     * Set a property 'prop' which has a derived / calculated property 'calc'.
     *   firePropertyChange(List.of(derived(CALC, getCalc(), () -> getCalc())) //
     *                    , PROP, this.prop, this.prop = v);
     *
     * The sequence of parameters makes sure that evaluation and update happens in the right order.
     * Preferably the derived would be at the end (Derived...) but that won't work correctly.
     *
     * @param derivedList
     * @param name
     * @param before
     * @param after
     * @param <T>
     */
    public <T> void firePropertyChange(List<Derived<T>> derivedList, String name, Object before, Object after) {
        firePropertyChange(name, before, after);
        for (Derived<?> derived : derivedList) {
            firePropertyChange(derived.name(), derived.beforeValue(), derived.afterCallable().get());
        }
    }

    protected record Derived<T>(String name, T beforeValue, Supplier<T> afterCallable) {}
    protected <T> Derived<T> derived(String name, T beforeValue, Supplier<T> afterCallable) {
        return new Derived(name, beforeValue, afterCallable);
    }

    // ===============================================================================================
    // VetoableChange

    transient volatile private VetoableChangeSupport vetoableChangeSupport = null;

    /**
     * VetoableChange
     */
    synchronized public boolean hasVetoableChangeListeners() {
        return (vetoableChangeSupport != null && vetoableChangeSupport.getVetoableChangeListeners().length > 0);
    }

    synchronized public boolean hasVetoableChangeListeners(String property) {
        return (vetoableChangeSupport != null && vetoableChangeSupport.hasListeners(property));
    }

    @Override
    synchronized public VetoableChangeListener[] getVetoableChangeListeners() {
        if (vetoableChangeSupport == null) {
            return new VetoableChangeListener[]{};
        }
        return vetoableChangeSupport.getVetoableChangeListeners();
    }

    @Override
    synchronized public VetoableChangeListener[] getVetoableChangeListeners(String propertyName) {
        if (vetoableChangeSupport == null) {
            return new VetoableChangeListener[]{};
        }
        return vetoableChangeSupport.getVetoableChangeListeners(propertyName);
    }

    @Override
    synchronized public void addVetoableChangeListener(VetoableChangeListener o) {
        if (vetoableChangeSupport == null) {
            vetoableChangeSupport = new VetoableChangeSupport(this);
        }
        vetoableChangeSupport.addVetoableChangeListener(o);
    }

    @Override
    synchronized public void addVetoableChangeListener(String property, VetoableChangeListener o) {
        if (vetoableChangeSupport == null) {
            vetoableChangeSupport = new VetoableChangeSupport(this);
        }
        vetoableChangeSupport.addVetoableChangeListener(property, o);
    }

    @Override
    synchronized public void removeVetoableChangeListener(VetoableChangeListener o) {
        if (vetoableChangeSupport == null) {
            return;
        }
        vetoableChangeSupport.removeVetoableChangeListener(o);
    }

    @Override
    synchronized public void removeVetoableChangeListener(String property, VetoableChangeListener o) {
        if (vetoableChangeSupport == null) {
            return;
        }
        vetoableChangeSupport.removeVetoableChangeListener(property, o);
    }

    @Override
    public void fireVetoableChange(String name, Object before, Object after) {
        if (vetoableChangeSupport == null) {
            return;
        }

        // do not fire if equal
        if (Objects.equals(before, after)) {
            return;
        }

        fireVetoableChangeAlways(name, before, after);
    }

    /**
     * fires even if the values are equal
     * @param name
     * @param before
     * @param after
     */
    public void fireVetoableChangeAlways(String name, Object before, Object after) {
        if (vetoableChangeSupport == null) {
            return;
        }

        // fire
        try {
            vetoableChangeSupport.fireVetoableChange(name, before, after);
        }
        catch (java.beans.PropertyVetoException e) {
            // JGoodies swallows PVE's, so make it a IAE: ExceptionUtil.throwUnchecked(e);
            throw new IllegalArgumentException("Vetoed:" + e.getMessage(), e);
        }
    }
}

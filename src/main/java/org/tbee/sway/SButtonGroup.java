package org.tbee.sway;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Like ButtonGroup, but associate values with the buttons, and base the API one the value.
 * <br/>
 * <br/>
 * Basic use:
 * <pre>{@code
 * var sButtonGroup = new SButtonGroup<Integer>() //
 *        .add(button1, 1) //
 *        .add(button2, 2) //
 *        .add(button3, 3) //
 *        .value(2);
 * sButtonGroup.getValue();
 * }
 * </pre>
 */
public class SButtonGroup<T> extends ButtonGroup {

    /**
     * Creates an empty <code>SButtonGroup</code>
     */
    public SButtonGroup() {
        super();
    }

    // ===================================================
    // BUTTONS

    /**
     * Adds a button to the group
     *
     * @param button an <code>AbstractButton</code> reference
     * @return
     */
    public SButtonGroup<T> add(AbstractButton button, T value) {
        if (button == null) {
            throw new IllegalArgumentException("Button is null");
        }
        if (buttons.contains(button)) {
            throw new IllegalArgumentException("Button is already present");
        }
        super.add(button);
        bindValue(button, value);
        button.addActionListener(actionListener);
        button.setEnabled(this.enabled);
        return this;
    }

    /**
     * Removes a button from the group
     *
     * @param button
     *            the button to be removed
     */
    public void remove(AbstractButton button) {
        if (button != null) {
            unbindValue(button);
            button.removeActionListener(actionListener);
            super.remove(button);
        }
    }

    /**
     * Returns the buttons in the group as a <code>List</code>
     *
     * @return a <code>List</code> containing the buttons in the group, in the
     *         order they were added to the group
     */
    public List<AbstractButton> getButtons() {
        return Collections.unmodifiableList(buttons);
    }

    /** store the relation between button and value */
    public void bindValue(AbstractButton button, T value) {
        if (!buttons.contains(button)) {
            throw new IllegalStateException("Cannot bind a button that is not part of the buttongroup");
        }
        buttonToValue.put(button, value);
        valueToButton.put(value, button);
    }

    /**
     *
     * @param button
     */
    private void unbindValue(AbstractButton button) {
        buttonToValue.remove(button);
        valueToButton.remove(button);
    }

    final private Map<AbstractButton, T> buttonToValue = new HashMap<AbstractButton, T>();
    final private Map<T, AbstractButton> valueToButton = new HashMap<T, AbstractButton>();


    // ===============================
    // JavaBean

    /**
     * Enabled
     *
     * @param value
     */
    public void setEnabled(boolean value) {
        this.enabled = value;
        for (int i = 0; i < getButtonCount(); i++) {
            AbstractButton lAbstractButton = (AbstractButton) getButtons().get(i);
            lAbstractButton.setEnabled(value);
        }
    }
    public boolean isEnabled() {
        return this.enabled;
    }
    boolean enabled = true;

    // ===============================
    // VALUE

    /** get the value of the selected button */
    public void setValue(T value) {
        setSelected(valueToButton.get(value).getModel(), true);
        fireValueChangedIfRequired();
    }
    public T getValue() {
        return buttonToValue.get(getSelectedButton());
    }
    final static public String VALUE = "value";
    public SButtonGroup<T> value(T v) {
        setValue(v);
        return this;
    }


    private final ActionListener actionListener = e -> fireValueChangedIfRequired();

    public void fireValueChangedIfRequired() {
        T lOldValue = buttonToValue.get(previousSelectedButton);
        T lNewValue = buttonToValue.get(getSelectedButton());
        previousSelectedButton = getSelectedButton();
        firePropertyChange(VALUE, lOldValue, lNewValue);
    }
    private AbstractButton previousSelectedButton = null;

    private AbstractButton getSelectedButton() {
        for (int i = 0; i < getButtonCount(); i++) {
            AbstractButton lAbstractButton = (AbstractButton) getButtons().get(i);
            if (lAbstractButton.isSelected()) {
                return lAbstractButton;
            }
        }
        return null;
    }


    // ===============================================================================================
    // PropertyChange (this class cannot extend AbstractBean)

    /** PropertyChange */
    public PropertyChangeListener[] getPropertyChangeListeners() {
        if (propertyChangeSupport == null) {
            return new PropertyChangeListener[]{};
        }
        return propertyChangeSupport.getPropertyChangeListeners();
    }

    synchronized public void addPropertyChangeListener(PropertyChangeListener o) {
        if (propertyChangeSupport == null) {
            propertyChangeSupport = new PropertyChangeSupport(this);
        }
        propertyChangeSupport.addPropertyChangeListener(o);
    }

    public void removePropertyChangeListener(PropertyChangeListener o) {
        if (propertyChangeSupport == null) {
            return;
        }
        propertyChangeSupport.removePropertyChangeListener(o);
    }

    public PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
        if (propertyChangeSupport == null) {
            return new PropertyChangeListener[] {};
        }
        return propertyChangeSupport.getPropertyChangeListeners(propertyName);
    }

    synchronized public void addPropertyChangeListener(String propertyName, PropertyChangeListener o) {
        if (propertyChangeSupport == null) {
            propertyChangeSupport = new PropertyChangeSupport(this);
        }
        propertyChangeSupport.addPropertyChangeListener(propertyName, o);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener o) {
        if (propertyChangeSupport == null) {
            return;
        }
        propertyChangeSupport.removePropertyChangeListener(propertyName, o);
    }

    public void firePropertyChange(String name, T before, T after) {
        // do the property change
        if (propertyChangeSupport == null) {
            return;
        }

        // fire
        propertyChangeSupport.firePropertyChange(name, before, after);
    }

    transient private PropertyChangeSupport propertyChangeSupport = null;
}

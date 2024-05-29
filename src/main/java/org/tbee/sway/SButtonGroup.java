package org.tbee.sway;

import org.tbee.sway.binding.BindingEndpoint;
import org.tbee.sway.binding.ExceptionHandler;
import org.tbee.sway.format.Format;
import org.tbee.sway.format.FormatRegistry;
import org.tbee.sway.mixin.BindToMixin;
import org.tbee.sway.mixin.ExceptionHandlerMixin;
import org.tbee.sway.mixin.PropertyChangeListenerMixin;
import org.tbee.sway.mixin.ValueMixin;
import org.tbee.util.ExceptionUtil;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.FocusManager;
import javax.swing.JOptionPane;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

// TODO:
// - implement logic ourselves, and extend AbstractBean?

/**
 * Like ButtonGroup, but associate values with the buttons, and base the API on the value.
 * <br/>
 * <br/>
 * Basic use:
 * <pre>{@code
 * var sButtonGroup = new SButtonGroup<City>() //
 *        .add(amsterdam, new SToggleButton("Amsterdam")) //
 *        .add(berlin, new SToggleButton("Berlin")) //
 *        .add(rome, new SToggleButton("Rome)) //
 *        .value(2);
 * sButtonGroup.getValue();
 * }
 * </pre>
 * Or with binding:
 * <pre>{@code
 * race.setPosition(1);
 * var sButtonGroup = new SButtonGroup<Integer>() //
 *        .add(1, new SRadioButton("1")) //
 *        .add(2, new SRadioButton("2") //
 *        .add(3, new SRadioButton("3") //
 *        .bindTo(race, "position");
 * }
 * </pre>
 * Or combined with Format and FormatRegistry:
 * <pre>{@code
 * var sButtonGroup = SButtonGroup.of(() -> new SRadioButton(), berlin, amsterdam, rome, paris);
 * }
 * </pre>
 */
public class SButtonGroup<T> extends ButtonGroup implements
        PropertyChangeListenerMixin<SButtonGroup<T>>,
        ExceptionHandlerMixin<SButtonGroup<T>>,
        ValueMixin<SButtonGroup<T>, T>,
        BindToMixin<SButtonGroup<T>, T> {

    static private org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SButtonGroup.class);

    /**
     * Creates an empty <code>SButtonGroup</code>
     */
    public SButtonGroup() {
        super();
    }


    // ===========================================================================================================================
    // For Mixins

    @Override
    public BindingEndpoint<T> defaultBindingEndpoint() {
        return value$();
    }


    // ===================================================
    // BUTTONS

    /**
     * Adds a button to the group
     *
     * @param button an <code>AbstractButton</code> reference
     * @return
     */
    public SButtonGroup<T> add(T value, AbstractButton button) {
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
    public SButtonGroup<T> enabled(boolean v) {
        setEnabled(v);
        return this;
    }

    // ===============================
    // VALUE

    /** get the value of the selected button */
    @Override
    public void setValue(T value) {
        setSelected(valueToButton.get(value).getModel(), true);
        fireValueChangedIfRequired();
    }
    public T getValue() {
        return buttonToValue.get(getSelectedButton());
    }

    private final ActionListener actionListener = e -> fireValueChangedIfRequired();

    public void fireValueChangedIfRequired() {
        T oldValue = buttonToValue.get(previousSelectedButton);
        T newValue = buttonToValue.get(getSelectedButton());
        previousSelectedButton = getSelectedButton();
        firePropertyChange(VALUE, oldValue, newValue);
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


    // ===========================================================================================================================
    // CONVENIENCE

    static public <T> SButtonGroup<T> of() {
        return new SButtonGroup<T>();
    }

    /**
     * Create a buttongroup using a format and factory (supplier)
     * @param format
     * @param supplier creates a button component for each value
     * @param values
     * @return
     * @param <T>
     */
    public static <T> SButtonGroup<T> of(Format<T> format, Supplier<AbstractButton> supplier, T... values) {
        var sButtonGroup = new SButtonGroup<T>();
        for (T value : values) {
            AbstractButton button = supplier.get();
            button.setText(format.toString(value));
            button.setIcon(format.toIcon(value));
            button.setHorizontalAlignment(format.horizontalAlignment().getSwingConstant());
            sButtonGroup.add(value, button);
        }
        return sButtonGroup;
    }

    /**
     * Create a buttongroup using FormatRegistry and factory (supplier)
     * @param supplier creates a button component for each value
     * @param values, cannot be empty
     * @return
     * @param <T>
     */
    public static <T> SButtonGroup<T> of(Class<T> clazz, Supplier<AbstractButton> supplier, T... values) {
        Format<T> format = (Format<T>) FormatRegistry.findFor(clazz);
        return of(format, supplier, values);
    }

    /**
     * Create a buttongroup using FormatRegistry and factory (supplier)
     * @param supplier creates a button component for each value
     * @param values, cannot be empty
     * @return
     * @param <T>
     */
    public static <T> SButtonGroup<T> of(Supplier<AbstractButton> supplier, T... values) {
        Format<T> format = (Format<T>) FormatRegistry.findFor(values[0].getClass());
        return of(format, supplier, values);
    }

    /**
     * Create a buttongroup with radiobuttons using FormatRegistry
     * @param values, cannot be empty
     * @return
     * @param <T>
     */
    public static <T> SButtonGroup<T> ofRadioButtons(T... values) {
        return of(() -> new SRadioButton(), values);
    }

    /**
     * Create a buttongroup with toggle buttons using FormatRegistry
     * @param values, cannot be empty
     * @return
     * @param <T>
     */
    public static <T> SButtonGroup<T> ofToggleButtons(T... values) {
        return of(() -> new SToggleButton(), values);
    }

    // ===============================================================================================
    // PropertyChange (this class cannot extend AbstractBean because it extends ButtonGroup)

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

    public void firePropertyChange(String name, Object before, Object after) {
        // do the property change
        if (propertyChangeSupport == null) {
            return;
        }

        // fire
        propertyChangeSupport.firePropertyChange(name, before, after);
    }

    transient private PropertyChangeSupport propertyChangeSupport = null;

    // ========================================================
    // EXCEPTION HANDLER

    /**
     * Set the ExceptionHandler used a.o. in binding
     * @param v
     */
    public void setExceptionHandler(ExceptionHandler v) {
        firePropertyChange(EXCEPTIONHANDLER, exceptionHandler, exceptionHandler = v);
    }
    public ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }
    private ExceptionHandler exceptionHandler = this::handleException;

    public boolean handleException(Throwable e) {

        // Display the error
        if (LOGGER.isDebugEnabled()) LOGGER.debug(e.getMessage(), e);
        JOptionPane.showMessageDialog(buttons.isEmpty() ? FocusManager.getCurrentManager().getActiveWindow() : buttons.get(0), ExceptionUtil.determineMessage(e), "ERROR", JOptionPane.ERROR_MESSAGE);

        // Mark exception as handled
        return true;
    }

    // ========================================================
    // FLUENT API
}

package org.tbee.sway;

import com.jgoodies.binding.beans.PropertyConnector;
import org.tbee.sway.binding.BeanBinder;
import org.tbee.sway.format.Format;
import org.tbee.sway.format.FormatRegistry;
import org.tbee.sway.format.JavaFormat;
import org.tbee.sway.format.StringFormat;
import org.tbee.sway.support.FocusInterpreter;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.List;
import java.util.*;

// TODO
// - visualize errors
// - error callback
// - enforce maximum length
// - color the contents based on the content, e.g. < 0 is red > 0 is black for a IntegerFormat
// - binding (to property and jgoodies)
//   - fireVetoableChange("name", this.name, name);
//   - firePropertyChange("name", this.name, this.name = name);
// undo

/**
 *
 * @param <T>
 */
public class STextField<T> extends javax.swing.JTextField {

    final private Format<T> format;

    /**
     *
     * @param format
     */
    public STextField(Format<T> format) {
        if (format == null) {
            throw new IllegalArgumentException("Null not allowed for format");
        }
        this.format = format;
        setColumns(format.columns());
        setHorizontalAlignment(format.horizontalAlignment());
        construct();
    }

    private void construct() {
        // the FocusInterpreterListener must be kept in an instance variable, otherwise it will be cleared by the WeakArrayList used in the FocusInterpreter
        focusInterpreterListener = evt -> {
            if (evt.getState() == FocusInterpreter.State.FOCUS_LOST) {
                // force a validation
                getValue();
            }
        };
        focusInterpreter.addFocusListener(focusInterpreterListener);
    }
    private FocusInterpreter.FocusInterpreterListener focusInterpreterListener = null;
    final private FocusInterpreter focusInterpreter = new FocusInterpreter(this);

    // ========================================================
    // OF

    static public <T> STextField<T> of(Class<T> clazz) {
        Format<T> format = (Format<T>) FormatRegistry.findFor(clazz);
        if (format == null) {
            throw new IllegalArgumentException("No format found for " + clazz);
        }
        return new STextField<T>(format);
    }

    static public STextField<String> ofString() {
        return of(String.class);
    }
    static public STextField<String> ofStringBlankIsNull() {
        return new STextField<String>(new StringFormat(true));
    }
    static public STextField<Integer> ofInteger() {
        return of(Integer.class);
    }
    static public STextField<BigInteger> ofBigInteger() {
        return of(BigInteger.class);
    }
    static public STextField<BigDecimal> ofBigDecimal() {
        return of(BigDecimal.class);
    }
    static public STextField<Number> ofPercent() {
        return new STextField<Number>(new JavaFormat<Number>(NumberFormat.getPercentInstance(), ("" + Double.MIN_VALUE).length(), SwingConstants.TRAILING));
    }
    static public STextField<Number> ofCurrency() {
        return new STextField<Number>(new JavaFormat<Number>(NumberFormat.getCurrencyInstance(), ("" + Double.MIN_VALUE).length() + 1, SwingConstants.TRAILING));
    }
    static public STextField<Number> ofCurrency(Locale locale) {
        return new STextField<Number>(new JavaFormat<Number>(NumberFormat.getCurrencyInstance(locale), ("" + Double.MIN_VALUE).length() + 4, SwingConstants.TRAILING));
    }
    static public STextField<Number> ofCurrency(Currency currency) {
        NumberFormat currencyInstance = NumberFormat.getCurrencyInstance();
        currencyInstance.setCurrency(currency);
        return new STextField<Number>(new JavaFormat<Number>(currencyInstance, ("" + Double.MIN_VALUE).length() + 4, SwingConstants.TRAILING));
    }


    // ========================================================
    // VALUE

    private T value = null;
    final static public String VALUE = "value";

    protected void setTextFromValue(T value) {
        super.setText(format.toString(value));
    }
    protected T getValueFromText() {
        String text = getText();
        T value = (format.toValue(text));
        return value;
    }

    /** Value (through Format) */
    public void setValue(T v) {
        System.out.println(getName() + " setValue " + v);

        // set value
        Object oldValue = this.value;
        this.value = v;

        // convert to text
        setTextFromValue(v);

        // fire PCE
        if (!Objects.equals(oldValue, this.value)) {
            System.out.println(getName() + " firePropertyChange " + VALUE + ": " + oldValue + " -> " + v);
            firePropertyChange(VALUE, oldValue, v); // fire a PCE for easy binding
        }
    }
    public STextField<T> value(T value) {
        setValue(value);
        return this;
    }

    /**
     *
     * @return
     */
    public T getValue() {
        try {
            T value = getValueFromText();
            setValue(value); // This will validate, reformat, send events, update this.value, etc.
        }
        catch (Exception e) {
            // set the value back to the latest value
            setValue(this.value);
        }
        return this.value;
    }

    // ==============================================
    // FLUENT API

    /** */
    public STextField<T> name(String v) {
        setName(v);
        return this;
    }

    /**  */
    public STextField<T> columns(int value) {
        setColumns(value);
        return this;
    }

    /**  */
    public STextField<T> font(Font value) {
        setFont(value);
        return this;
    }

    /**  */
    public STextField<T> enabled(boolean enabled) {
        setEnabled(enabled);
        return this;
    }

    /**  */
    public STextField<T> toolTipText(String text) {
        setToolTipText(text);
        return this;
    }

    /**  */
    public STextField<T> editable(boolean enabled) {
        setEditable(enabled);
        return this;
    }

    // ========================================================
    // BIND

    private List<PropertyConnector> propertyConnectors;

    /**
     *
     * @param bean
     * @param propertyName
     * @return
     */
    public STextField<T> bind(Object bean, String propertyName) {

        // Bind
        PropertyConnector propertyConnector = PropertyConnector.connect(bean, propertyName, this, VALUE);
        propertyConnector.updateProperty2();

        // Remember binding (for unbinding)
        if (propertyConnectors == null) {
            propertyConnectors = new ArrayList<>();
        }
        propertyConnectors.add(propertyConnector);

        return this;
    }

    /**
     *
     * @param bean
     * @param propertyName
     * @return
     */
    public boolean unbind(Object bean, String propertyName) {
        if (propertyConnectors == null) {
            return false;
        }

        // Find the PropertyConnector
        List<PropertyConnector> toBeRemovedPropertyConnectors = propertyConnectors.stream() //
                .filter(pc -> pc.getBean1().equals(bean) && pc.getProperty1Name().equals(propertyName)) //
                .toList();

        // Unbind the PropertyConnector
        toBeRemovedPropertyConnectors.stream().forEach(pc -> pc.release());
        propertyConnectors.removeAll(toBeRemovedPropertyConnectors);

        // Done
        return toBeRemovedPropertyConnectors.size() > 0;
    }

    /**
     * Beware: this binding cannot be unbound! You can change the bound bean by replacing the value in de beanBinder.
     * @param beanBinder
     * @param propertyName
     * @return
     */
    public STextField<T> bind(BeanBinder beanBinder, String propertyName) {
        beanBinder.bind(propertyName, this, VALUE);
        return this;
    }
}

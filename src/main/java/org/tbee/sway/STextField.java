package org.tbee.sway;

import org.tbee.sway.format.Format;
import org.tbee.sway.format.FormatRegistry;
import org.tbee.sway.format.JavaFormat;
import org.tbee.sway.format.StringFormat;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.Objects;

// TODO
// - parse value on focus lost
// - visualize errors
// - error callback
// - enforce maximum length
// - color the contents based on the content, e.g. < 0 is red > 0 is black for a IntegerFormat
// - binding (to property and jgoodies)
//   - fireVetoableChange("name", this.name, name);
//   - firePropertyChange("name", this.name, this.name = name);

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
    }

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
    final static public String VALUE_PROPERTY = "value";

    protected void setTextFromValue(T value) {
        super.setText(format.toString(value));
    }
    protected T getValueFromText() {
        String text = getText();
        T value = (format.toValue(text));
        return value;
    }

    /** Value (through Format) */
    public void setValue(T value) {

        // set value
        Object oldValue = this.value;
        this.value = value;

        // convert to text
        setTextFromValue(value);

        // fire PCE
        if (!Objects.equals(oldValue, this.value)) {
            firePropertyChange(VALUE_PROPERTY, oldValue, value); // fire a PCE for easy binding

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
            setValue(value); // This will validate nullAlowed, reformat, send events, update this.value, etc.
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

}
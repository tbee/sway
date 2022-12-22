package org.tbee.sway;

import com.jgoodies.binding.beans.PropertyConnector;
import org.tbee.sway.binding.BeanBinder;
import org.tbee.sway.format.*;
import org.tbee.sway.support.FocusInterpreter;
import org.tbee.util.ExceptionUtil;
import org.tbee.util.MinimalPropertyChangeProvider;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.*;

// TODO
// - error callback
// - enforce maximum length
// - color the contents based on the content, e.g. < 0 is red > 0 is black for a IntegerFormat
// - undo
// - popup
// - format for: Double, Long

/**
 *
 * @param <T>
 */
public class STextField<T> extends javax.swing.JTextField {
    static private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(STextField.class);

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
    static public STextField<LocalDate> ofLocalDate() {
        return of(LocalDate.class);
    }
    static public STextField<LocalDate> ofLocalDate(FormatStyle formatStyle, Locale locale) {
        return new STextField<LocalDate>(new LocalDateFormat(formatStyle, locale));
    }
    static public STextField<LocalDate> ofLocalDate(FormatStyle formatStyle) {
        return new STextField<LocalDate>(new LocalDateFormat(formatStyle));
    }
    static public STextField<LocalDate> ofLocalDate(Locale locale) {
        return new STextField<LocalDate>(new LocalDateFormat(locale));
    }
    static public STextField<LocalDateTime> ofLocalDateTime() {
        return of(LocalDateTime.class);
    }
    static public STextField<LocalDateTime> ofLocalDateTime(FormatStyle formatStyle, Locale locale) {
        return new STextField<LocalDateTime>(new LocalDateTimeFormat(formatStyle, locale));
    }
    static public STextField<LocalDateTime> ofLocalDateTime(FormatStyle formatStyle) {
        return new STextField<LocalDateTime>(new LocalDateTimeFormat(formatStyle));
    }
    static public STextField<LocalDateTime> ofLocalDateTime(Locale locale) {
        return new STextField<LocalDateTime>(new LocalDateTimeFormat(locale));
    }
    static public STextField<ZonedDateTime> ofZonedDateTime() {
        return of(ZonedDateTime.class);
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

        // set value
        Object oldValue = this.value;
        this.value = v;

        // convert to text
        setTextFromValue(v);

        // fire PCE
        if (!Objects.equals(oldValue, this.value)) {
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

            // Force focus back
            SwingUtilities.invokeLater(() -> this.grabFocus());

            // Display the error
            if (logger.isInfoEnabled()) logger.info(e.getMessage(), e);
            JOptionPane.showMessageDialog(this, ExceptionUtil.determineMessage(e), "ERROR", JOptionPane.ERROR_MESSAGE);
        }

        return this.value;
    }

    protected void processKeyEvent(KeyEvent e) {
        // on ESC set the value back to the latest value
        if (e.getKeyCode() == 27 && e.getID() == KeyEvent.KEY_PRESSED) {
            setValue(this.value);
        }
        super.processKeyEvent(e);
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
     * Will create a binding to a specific bean/property.
     * Use bind(BeanBinding, PropertyName) to be able to switch beans while keeping the bind.
     *
     * @param bean
     * @param propertyName
     * @return
     */
    public STextField<T> bind(MinimalPropertyChangeProvider bean, String propertyName) {

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
     * Will unbind all connection to the bean/property combination.
     *
     * @param bean
     * @param propertyName
     * @return true if unbind was successful
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
     * Bind to a bean wrapper's property.
     * This will allow the swap the bean (in the BeanBinder) without having to rebind.
     *
     * Beware: this binding cannot be unbound! The bean in the BeanBinder can be set to null only.
     *
     * @param beanBinder
     * @param propertyName
     * @return
     */
    public STextField<T> bind(BeanBinder beanBinder, String propertyName) {
        beanBinder.bind(propertyName, this, VALUE);
        return this;
    }
}

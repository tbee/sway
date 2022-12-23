package org.tbee.sway;

import com.jgoodies.binding.beans.PropertyConnector;
import org.tbee.sway.binding.BeanBinder;
import org.tbee.sway.binding.ExceptionCatcher;
import org.tbee.sway.format.Format;
import org.tbee.sway.format.FormatRegistry;
import org.tbee.sway.format.JavaFormat;
import org.tbee.sway.format.StringFormat;
import org.tbee.sway.support.FocusInterpreter;
import org.tbee.util.ExceptionUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

// TODO
// - error when setValue when bound
// - popup
// - enforce maximum length
// - color the contents based on the content, e.g. < 0 is red > 0 is black for a IntegerFormat
// - undo

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
            if (evt.getState() == FocusInterpreter.State.LOSING_FOCUS) {
                setValueFromText();
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
    static public STextField<Long> ofLong() {
        return of(Long.class);
    }
    static public STextField<Double> ofDouble() {
        return of(Double.class);
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
    static public STextField<Number> ofPercent(Locale locale) {
        return new STextField<Number>(new JavaFormat<Number>(NumberFormat.getPercentInstance(locale), ("" + Double.MIN_VALUE).length(), SwingConstants.TRAILING));
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
    static public STextField<LocalDateTime> ofLocalDateTime() {
        return of(LocalDateTime.class);
    }
    static public STextField<ZonedDateTime> ofZonedDateTime() {
        return of(ZonedDateTime.class);
    }
    static public STextField<OffsetDateTime> ofOffsetDateTime() {
        return of(OffsetDateTime.class);
    }


    // ========================================================
    // VALUE

    private T value = null;
    final static public String VALUE = "value";

    protected void setTextFromValue(T value) {
        String text = format.toString(value);
        super.setText(text);
    }
    protected T setValueFromText() {
        try {
            String text = getText();
            T value = format.toValue(text);
            setValue(value); // This will validate, reformat, send events, update this.value, etc.
        }
        catch (RuntimeException e) {
            handleException(e);
        }
        return this.value;
    }

    protected boolean handleException(Throwable e, Object oldValue, Object newValue) {
        return handleException(e);
    }
    protected boolean handleException(Throwable e) {

        // Force focus back
        SwingUtilities.invokeLater(() -> this.grabFocus());

        // Display the error
        if (logger.isDebugEnabled()) logger.debug(e.getMessage(), e);
        JOptionPane.showMessageDialog(this, ExceptionUtil.determineMessage(e), "ERROR", JOptionPane.ERROR_MESSAGE);

        // Mark exception as handled
        return true;
    }

    /** Value (through Format) */
    public void setValue(T v) {
        setTextFromValue(v);
        try {
            firePropertyChange(VALUE, this.value, this.value = v);
        }
        catch (RuntimeException e) {
            handleException(e);
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
    public STextField<T> bind(Object bean, String propertyName) {

        // Bind
        PropertyConnector propertyConnector = PropertyConnector.connect(new ExceptionCatcher(bean, propertyName, this::handleException), ExceptionCatcher.VALUE, this, VALUE);
        propertyConnector.updateProperty2();

        // Remember binding (for unbinding)
        if (propertyConnectors == null) {
            propertyConnectors = new ArrayList<>();
        }
        propertyConnectors.add(propertyConnector);

        // Done
        return this;
    }

    /**
     * Will unbind the connection to the bean/property combination.
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
     * @param beanBinder
     * @param propertyName
     * @return
     */
    public STextField<T> bind(BeanBinder beanBinder, String propertyName) {
        return bind(beanBinder.getBeanAdapter().getValueModel(propertyName), "value");
    }

    /**
     * Will unbind the connection to the beanBinder/property combination.
     *
     * @param beanBinder
     * @param propertyName
     * @return true if unbind was successful
     */
    public boolean unbind(BeanBinder beanBinder, String propertyName) {
        return unbind(beanBinder.getBeanAdapter().getValueModel(propertyName), "value");
    }
}

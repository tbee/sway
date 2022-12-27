package org.tbee.sway;

import com.jgoodies.binding.beans.PropertyConnector;
import org.tbee.sway.binding.BeanBinder;
import org.tbee.sway.binding.ExceptionCatcher;
import org.tbee.sway.format.Format;
import org.tbee.sway.format.FormatRegistry;
import org.tbee.sway.format.JavaFormat;
import org.tbee.sway.format.StringFormat;
import org.tbee.sway.support.FocusInterpreter;
import org.tbee.util.ClassUtil;
import org.tbee.util.ExceptionUtil;

import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

// TODO
// - error when setValue when bound
// - ofBind()
// - popup
// - enforce maximum length
// - color the contents based on the content, e.g. < 0 is red > 0 is black for a IntegerFormat
// - undo

/**
 * A strongly typed text field with optional binding to JavaBeans.
 * The value (not the text) in the textfield can be accessed via getValue/setValue.
 * This textfield uses a format to convert between the text and the value.
 * <br/>
 * <br/>
 * Basic use:
 * <pre>{@code
 * var sTextField = new STextField<Integer>(new IntegerFormat());
 * sTextField.setValue(123);
 * }
 * </pre>
 *
 * <h2>Convenience methods</h2>
 * STextField has a number of convenience methods.
 * These for the most part rely on a central administration of formats (see org.tbee.sway.format.FormatRegistry).
 * Some the methods contain hardcoded formats, e.g. percent and currency, because there are no dedicated types registered.
 * However, it is possible to implement a ValueObject 'Currency' in your domain model and register a Format for it.
 * (The Currency in the JRE is not a ValueObject).
 * <br/>
 * <br/>
 * Example:
 * <pre>{@code
 * var sTextField1 = STextField.of(Integer.class);
 * var sTextField2 = STextField.ofInteger();
 * var sTextField3 = STextField.ofString();
 * var sTextField4 = STextField.ofLocalDate();
 * var sTextField4 = STextField.ofCurrency(Currency.getInstance("EUR"));
 * ...
 * }
 * </pre>
 *
 * <h2>Binding</h2>
 * STextFields can be bound to objects implementing the JavaBeans standard,
 * most notably addPropertyChangeListener, removePropertyChangeListener, and it should fire appropriate PropertyChangeEvents.
 * Binding can be done in two ways.
 * <br/>
 * <br/>
 * Directly to a bean:
 * <pre>{@code
 * var someBean = new SomeBean();
 * var sTextField1 = STextField.ofInteger().bind(someBean, "distance");
 * var sTextField2 = STextField.ofBind(someBean, "distance"); // determines property type and binds to it
 * }
 * </pre>
 *
 * Or via a BeanBinder:
 * <pre>{@code
 * var someBean = new SomeBean();
 * var someBeanBinder = new BeanBinder<SomeBean>(someBean);
 * var sTextField = STextField.ofInteger().bind(someBeanBinder, "distance");
 * var sTextField = STextField.ofBind(someBeanBinder, "distance"); // determines property type and binds to it
 * ...
 * var someBean2 = new SomeBean();
 * someBeanBinder.set(someBean2);
 * }
 * </pre>
 *
 * <h2>Error handling</h2>
 * Errors when typing an incorrect value will be displayed, and the incorrect text will remain in the textfield.
 * <br/>
 * Errors when setting a value through binding will be displayed, but the text will revert to the last valid value.
 *
 * @param <T> the type of value the textfield holds.
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

    /**
     * Determines the correct class, creates a TextField, and binds it to the property.
     * @param bean
     * @param propertyName
     * @return
     * @param <T>
     */
    static public <T> STextField<T> ofBind(Object bean, String propertyName) {
        try {
            // Use Java's bean inspection classes to analyse the bean
            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            PropertyDescriptor propertyDescriptor = Arrays.stream(propertyDescriptors) //
                    .filter(pd -> pd.getName().equals(propertyName)) //
                    .findFirst().orElse(null);
            if (propertyDescriptor == null) {
                throw new IllegalArgumentException("Property '" + propertyName + "' not found in bean " + bean.getClass());
            }

            // Handle primitive types
            Class<?> propertyType = propertyDescriptor.getPropertyType();
            if (propertyType.isPrimitive()) {
                propertyType = ClassUtil.primitiveToClass(propertyType);
            }

            // Create TextField
            return (STextField<T>) of(propertyType).bind(bean, propertyName);
        }
        catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }

    static public <T> STextField<T> ofBind(BeanBinder<T> beanBinder, String propertyName) {
        try {
            // Use Java's bean inspection classes to analyse the bean
            BeanInfo beanInfo = Introspector.getBeanInfo(beanBinder.get().getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            PropertyDescriptor propertyDescriptor = Arrays.stream(propertyDescriptors) //
                    .filter(pd -> pd.getName().equals(propertyName)) //
                    .findFirst().orElse(null);
            if (propertyDescriptor == null) {
                throw new IllegalArgumentException("Property '" + propertyName + "' not found in bean " + beanBinder.get().getClass());
            }

            // Handle primitive types
            Class<?> propertyType = propertyDescriptor.getPropertyType();
            if (propertyType.isPrimitive()) {
                propertyType = ClassUtil.primitiveToClass(propertyType);
            }

            // Create TextField
            return (STextField<T>) of(propertyType).bind(beanBinder, propertyName);
        }
        catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
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
        setValueFromText();
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

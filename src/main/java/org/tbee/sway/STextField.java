package org.tbee.sway;

import org.tbee.sway.binding.BeanBinder;
import org.tbee.sway.binding.BindingEndpoint;
import org.tbee.sway.binding.ExceptionHandler;
import org.tbee.sway.format.Format;
import org.tbee.sway.format.FormatRegistry;
import org.tbee.sway.format.JavaFormat;
import org.tbee.sway.format.StringFormat;
import org.tbee.sway.mixin.BindToMixin;
import org.tbee.sway.mixin.EditableMixin;
import org.tbee.sway.mixin.ExceptionHandlerDefaultMixin;
import org.tbee.sway.mixin.HAlignMixin;
import org.tbee.sway.mixin.JComponentMixin;
import org.tbee.sway.mixin.OverlayMixin;
import org.tbee.sway.mixin.ToolTipMixin;
import org.tbee.sway.support.FocusInterpreter;
import org.tbee.sway.support.HAlign;
import org.tbee.sway.text.DocumentFilterSize;
import org.tbee.util.ClassUtil;
import org.tbee.util.ExceptionUtil;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.text.AbstractDocument;
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
import java.util.Arrays;
import java.util.Currency;
import java.util.Locale;

// TODO
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
 * var sTextField1 = STextField.ofInteger().bindTo(someBean.distance$());
 * var sTextField2 = STextField.ofBindTo(someBean.distance$()); // determines property type and binds to it
 * }
 * </pre>
 *
 * Or via a BeanBinder:
 * <pre>{@code
 * var someBean = new SomeBean();
 * var someBeanBinder = new BeanBinder<SomeBean>(someBean);
 * var sTextField = STextField.ofInteger().bindTo(someBeanBinder, "distance");
 * var sTextField = STextField.ofBindTo(someBeanBinder, "distance"); // determines property type and binds to it
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
public class STextField<T> extends javax.swing.JTextField implements
        HAlignMixin<STextField<T>>,
        OverlayMixin<STextField<T>>,
        ToolTipMixin<STextField<T>>,
        BindToMixin<STextField<T>, T>,
        EditableMixin<STextField<T>>,
        ExceptionHandlerDefaultMixin<STextField<T>>,
        JComponentMixin<STextField<T>> {

    final static private org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(STextField.class);

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
        setHAlign(format.horizontalAlignment());

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

    // ===========================================================================================================================
    // For Mixins

    @Override
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        super.firePropertyChange(propertyName, oldValue, newValue);
    }

    @Override
    public BindingEndpoint<T> defaultBindingEndpoint() {
        return value$();
    }

    // ========================================================
    // OF

    static public <T> STextField<T> of(Format<T> format) {
        return new STextField<T>(format);
    }

    static public <T> STextField<T> of(Class<T> clazz) {
        Format<T> format = (Format<T>) FormatRegistry.findFor(clazz);
        if (format == null) {
            throw new IllegalArgumentException("No format found for " + clazz);
        }
        return of(format);
    }

    /**
     * Creates a TextField, and binds it to the value property.
     *
     * @param bindingEndpoint
     * @return
     * @param <T>
     */
    static public <T> STextField<T> ofBindTo(BindingEndpoint<T> bindingEndpoint) {
        if (bindingEndpoint.beanBinder() != null) {
            return (STextField<T>) ofBindTo(bindingEndpoint.beanBinder(), bindingEndpoint.propertyName());
        }
        return ofBindTo(bindingEndpoint.bean(), bindingEndpoint.propertyName());
    }

    static private <T> STextField<T> ofBindTo(Object bean, String propertyName) {
        Class<?> propertyType = determinePropertyType(bean.getClass(), propertyName);
        return (STextField<T>) of(propertyType).bindTo(BindingEndpoint.of(bean, propertyName));
    }

    static private <T> STextField<T> ofBindTo(BeanBinder<T> beanBinder, String propertyName) {
        Class<?> propertyType = determinePropertyType(beanBinder.get().getClass(), propertyName);
        return (STextField<T>) of(propertyType).bindTo(BindingEndpoint.of(beanBinder, propertyName));
    }

    static private <T> Class<?> determinePropertyType(Class<?> beanClass, String propertyName) {
        try {
            // Use Java's bean inspection classes to analyse the bean
            BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            PropertyDescriptor propertyDescriptor = Arrays.stream(propertyDescriptors) //
                    .filter(pd -> pd.getName().equals(propertyName)) //
                    .findFirst().orElse(null);
            if (propertyDescriptor == null) {
                throw new IllegalArgumentException("Property '" + propertyName + "' not found in bean " + beanClass);
            }

            // Handle primitive types
            Class<?> propertyType = propertyDescriptor.getPropertyType();
            if (propertyType.isPrimitive()) {
                propertyType = ClassUtil.primitiveToClass(propertyType);
            }

            // Create TextField
            return propertyType;
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
        return new STextField<Number>(new JavaFormat<Number>(NumberFormat.getPercentInstance(), ("" + Double.MIN_VALUE).length(), HAlign.TRAILING));
    }
    static public STextField<Number> ofPercent(Locale locale) {
        return new STextField<Number>(new JavaFormat<Number>(NumberFormat.getPercentInstance(locale), ("" + Double.MIN_VALUE).length(), HAlign.TRAILING));
    }
    static public STextField<Number> ofCurrency() {
        return new STextField<Number>(new JavaFormat<Number>(NumberFormat.getCurrencyInstance(), ("" + Double.MIN_VALUE).length() + 1, HAlign.TRAILING));
    }
    static public STextField<Number> ofCurrency(Locale locale) {
        return new STextField<Number>(new JavaFormat<Number>(NumberFormat.getCurrencyInstance(locale), ("" + Double.MIN_VALUE).length() + 4, HAlign.TRAILING));
    }
    static public STextField<Number> ofCurrency(Currency currency) {
        NumberFormat currencyInstance = NumberFormat.getCurrencyInstance();
        currencyInstance.setCurrency(currency);
        return new STextField<Number>(new JavaFormat<Number>(currencyInstance, ("" + Double.MIN_VALUE).length() + 4, HAlign.TRAILING));
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

    /**
     * Parses the text into the value.
     * @return true is the value was set, false if there was an error
     */
    public boolean setValueFromText() {
        try {
            String text = getText();
            T value = format.toValue(text);
            setValue(value); // This will validate, reformat, send events, update this.value, etc.
        }
        catch (RuntimeException e) {
            handleException(e);
            return false;
        }
        return true;
    }

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
        if (handlingExceptionCnt > 0 || !isShowing()) {
            return false;
        }

        try {
            handlingExceptionCnt++;

            // Force focus back
            SwingUtilities.invokeLater(() -> this.grabFocus());

            // Display the error
            if (LOGGER.isDebugEnabled()) LOGGER.debug(e.getMessage(), e);
            JOptionPane.showMessageDialog(this, ExceptionUtil.determineMessage(e), "ERROR", JOptionPane.ERROR_MESSAGE);

            // Mark exception as handled
            return true;
        }
        finally {
            handlingExceptionCnt--;
        }
    }
    private int handlingExceptionCnt = 0;

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
    public T getValue() {
        return this.value;
    }
    public STextField<T> value(T value) {
        setValue(value);
        return this;
    }
    public BindingEndpoint<T> value$() {
        return BindingEndpoint.of(this, VALUE, exceptionHandler);
    }

    protected void processKeyEvent(KeyEvent e) {
        // on ESC set the value back to the latest value
        if (e.getKeyCode() == 27 && e.getID() == KeyEvent.KEY_PRESSED) {
            setValue(this.value);
        }
        super.processKeyEvent(e);
    }

    // ==============================================
    // JavaBean

    /**
     * maxLength
     */
    public void setMaxLength(int value) {
        maxLength = value;
        if (documentFilterSize == null) {
            documentFilterSize = new DocumentFilterSize(this, () -> maxLength);
            ((AbstractDocument) getDocument()).setDocumentFilter(documentFilterSize);
        }
    }
    private DocumentFilterSize documentFilterSize = null;

    public int getMaxLength() { return maxLength; }
    public STextField<T> maxLength(int value) {
        setMaxLength(value);
        return this;
    }
    volatile private int maxLength = -1;
    final static public String MAXLENGTH_PROPERTY_ID = "maxLength";


    // ==============================================
    // FLUENT API

    public STextField<T> columns(int value) {
        setColumns(value);
        return this;
    }
}

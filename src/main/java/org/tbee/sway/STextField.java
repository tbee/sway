package org.tbee.sway;

import org.tbee.sway.format.Format;
import org.tbee.sway.format.JavaFormat;
import org.tbee.sway.format.StringFormat;

import java.awt.*;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

// TODO
// - alignment based on format
// - parse value on focus lost
// - visualize errors
// - error callback
// - enforce maximum length
// - color the contents based on the content, e.g. < 0 is red > 0 is black for a IntegerFormat

/**
 *
 * @param <T>
 */
public class STextField<T> extends javax.swing.JTextField {

    final private Format<T> format;

    public STextField(Format<T> format) {
        if (format == null) {
            throw new IllegalArgumentException("Null not allowed for format");
        }
        this.format = format;
        setColumns(format.columns());
    }

    /**
     * Register additional formats. These will override predefined ones.
     * @param clazz
     * @param format
     */
    static public void register(Class<?> clazz, Format<?> format) {
        formats.put(clazz, format);
    }
    static public boolean unregister(Format<?> format) {
        return formats.remove(format) != null;
    }
    private static Map<Class<?>, Format<?>> formats = new HashMap<>();

    static private Format<?> determineFormat(Class<?> clazz) {
        Format<?> format = formats.get(clazz);
        if (format != null) {
            return format;
        }

        if (clazz.equals(String.class)) return new StringFormat();
        if (clazz.equals(Integer.class)) return new JavaFormat<Integer>(NumberFormat.getIntegerInstance());
        throw new IllegalStateException("No format found for " + clazz);
    }

    static public <T> STextField<T> of(Class<T> clazz) {
        Format<T> format = (Format<T>) determineFormat(clazz);
        return new STextField<T>(format);
    }


    // ========================================================
    // VALUE

    private T value = null;
    final static public String VALUE_PROPERTY = "value";

    protected void setTextFromValue(T value) {
        super.setText(value == null ? "" : format.toString(value));
    }
    protected T getValueFromText() {
        String text = getText();
        T value = (text == null || text.length() == 0 ? null : format.toValue(text));
        return value;
    }

    /** NullAllowed */
    public boolean isNullAllowed() {
        return nullAllowed;
    }
    public void setNullAllowed(boolean value) {
        nullAllowed = value;
    }
    private boolean nullAllowed = true;
    public STextField<T> nullAllowed(boolean value) {
        setNullAllowed(value);
        return this;
    }

    /** Value (through Format) */
    public void setValue(T value) {

        // check
        if (value == null && nullAllowed == false) {
            setTextFromValue(value); // reset text
            throw new IllegalArgumentException("Null is not allowed as a value");
        }

        // set value
        String text = getText();
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

package org.tbee.sway;

import org.tbee.sway.binding.BindingEndpoint;
import org.tbee.sway.binding.ExceptionHandler;
import org.tbee.sway.format.Format;
import org.tbee.sway.format.IntegerFormat;
import org.tbee.sway.mixin.BindToMixin;
import org.tbee.sway.mixin.ExceptionHandlerDefaultMixin;
import org.tbee.sway.mixin.HAlignMixin;
import org.tbee.sway.mixin.JComponentMixin;
import org.tbee.sway.mixin.ValueMixin;

import javax.swing.AbstractSpinnerModel;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import java.awt.BorderLayout;
import java.text.ParseException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * THe SSpinner is always not editable, us a SComboBox is you want to be able to
 * @param <T>
 */
public class SSpinner<T> extends JPanel implements
        JComponentMixin<SSpinner<T>>,
        ValueMixin<SSpinner<T>, T>,
        ExceptionHandlerDefaultMixin<SSpinner<T>>,
        HAlignMixin<SSpinner<T>>,
        BindToMixin<SSpinner<T>, T> {

    private final JSpinner jSpinner = new JSpinner();
    private final MySpinnerModel spinnerModel = new MySpinnerModel();

    private T value = null;
    private Function<T, T> previousValueFunction = T -> T;
    private Function<T, T> nextValueFunction = T -> T;

    public SSpinner(T startValue) {
        jSpinner.setModel(spinnerModel);

        setLayout(new BorderLayout());
        add(jSpinner, BorderLayout.CENTER);

        setValue(startValue);
    }

    class MySpinnerModel extends AbstractSpinnerModel {

        @Override
        public Object getValue() {
            return SSpinner.this.value;
        }

        @Override
        public void setValue(Object value) {
            SSpinner.this.setValue((T)value);
        }

        @Override
        public Object getNextValue() {
            return nextValueFunction.apply(SSpinner.this.value);
        }

        @Override
        public Object getPreviousValue() {
            return previousValueFunction.apply(SSpinner.this.value);
        }

        protected void fireStateChanged() {
            super.fireStateChanged();
        }
    }

    public void refresh() {
        spinnerModel.fireStateChanged();
    }

    /** Value) */
    public void setValue(T v) {
        if (!Objects.equals(this.value, v)) {
            firePropertyChange(VALUE, this.value, this.value = v);
            spinnerModel.fireStateChanged();
        }
    }
    public T getValue() {
        return this.value;
    }

    public SSpinner<T> previousValueFunction(Function<T, T> v) {
        this.previousValueFunction = v;
        return this;
    }
    public SSpinner<T> nextValueFunction(Function<T, T> v) {
        this.nextValueFunction = v;
        return this;
    }


    // ===========================================================================
    // RENDERING

    /**
     * @param format
     * @return
     */
    public SSpinner<T> render(Format<T> format) {
        JFormattedTextField jFormattedTextField = getTextField();
        jFormattedTextField.setFormatterFactory(new JFormattedTextField.AbstractFormatterFactory() {
            @Override
            public JFormattedTextField.AbstractFormatter getFormatter(JFormattedTextField tf) {
                return new JFormattedTextField.AbstractFormatter() {

                    @Override
                    public Object stringToValue(String text) throws ParseException {
                        // If you can't edit, then it must be the current value, no reason to start parsing.
                        // This makes providing a Format in not editable mode much simpler, only toString needs to be implemented.
                        if (!isEditable()) {
                            return SSpinner.this.value;
                        }
                        try {
                            return format.toValue(text);
                        }
                        catch (Exception e) {
                            handleException(e);
                            throw e;
                        }
                    }

                    @Override
                    public String valueToString(Object value) throws ParseException {
                        try {
                            return format.toString((T)value);
                        }
                        catch (Exception e) {
                            handleException(e);
                            throw e;
                        }
                    }
                };
            }
        });

        if (format.columns() >= 0) {
            jFormattedTextField.setColumns(format.columns());
        }
        hAlign(format.horizontalAlignment());

        return this;
    }

    private JFormattedTextField getTextField() {
        JFormattedTextField f = ((JSpinner.DefaultEditor) jSpinner.getEditor()).getTextField();
        return f;
    }

    /**
     * @param clazz
     * @return
     */
    public SSpinner<T> renderFor(Class<T> clazz) {
        return render((Format<T>) SFormatRegistry.findFor(clazz));
    }

    // ===========================================================================================================================
    // For Mixins

    @Override
    public BindingEndpoint<T> defaultBindingEndpoint() {
        return value$();
    }

    @Override
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        super.firePropertyChange(propertyName, oldValue, newValue);
    }

    public int getHorizontalAlignment() {
        JFormattedTextField textField = getTextField();
        return textField.getHorizontalAlignment();
    }
    public void setHorizontalAlignment(int alignment) {
        JFormattedTextField textField = getTextField();
        textField.setHorizontalAlignment(alignment);
    }


    // ========================================================
    // PROPERTIES

    public boolean isEditable() {
        return getTextField().isEditable();
    }
    public void setEditable(boolean v) {
        boolean oldValue = isEditable();
        getTextField().setEditable(true);
        firePropertyChange(EDITABLE, oldValue, v);
    }
    static public String EDITABLE = "editable";
    public SSpinner<T> editable(boolean v) {
        setEditable(true);
        return this;
    }
    public BindingEndpoint<T> editable$() {
        return BindingEndpoint.of(this, EDITABLE);
    }

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


    // ========================================================
    // FLUENT API

    public SSpinner<T> columns(int columns) {
        getTextField().setColumns(columns);
        return this;
    }

    static public SSpinner<Integer> ofInteger() {
        return ofInteger(0);
    }
    static public SSpinner<Integer> ofInteger(int startValue) {
        return new SSpinner<>(startValue)
                .render(new IntegerFormat())
                .previousValueFunction(i -> {
                    if (i == Integer.MIN_VALUE) {
                        return i;
                    }
                    return i - 1;
                })
                .nextValueFunction(i -> {
                    if (i == Integer.MAX_VALUE) {
                        return i;
                    }
                    return i + 1;
                });
    }

    static public <T> SSpinner<T> of(List<T> values) {
        return of(0, values);
    }
    static public <T> SSpinner<T> of(int startIndex, List<T> values) {
        if (values.isEmpty()) {
            throw new IllegalArgumentException("List cannot be empty");
        }

        // In order to handle duplicates in the list correctly, the actual value is based on the index in the list
        AtomicInteger idx = new AtomicInteger(startIndex);

        SSpinner<T> spinner = new SSpinner<>(values.get(startIndex))
                .nextValueFunction(listEntry -> {
                    int i = idx.get() + 1;
                    if (i >= values.size()) {
                        i = values.size() - 1;
                    }
                    idx.set(i);
                    return values.get(i);
                })
                .previousValueFunction(listEntry -> {
                    int i = idx.get() - 1;
                    if (i < 0) {
                        i = 0;
                    }
                    idx.set(i);
                    return values.get(i);
                });

        // Make sure that a setValue updates the index
        spinner.value$().onChange((Consumer<T>)v -> idx.set(values.indexOf(v)));

        return spinner;
    }
}

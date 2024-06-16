package org.tbee.sway;

import org.tbee.sway.binding.BindingEndpoint;
import org.tbee.sway.binding.ExceptionHandler;
import org.tbee.sway.format.Format;
import org.tbee.sway.list.DefaultListCellRenderer;
import org.tbee.sway.mixin.BindToMixin;
import org.tbee.sway.mixin.DataMixin;
import org.tbee.sway.mixin.ExceptionHandlerMixin;
import org.tbee.sway.mixin.JComponentMixin;
import org.tbee.sway.mixin.ValueMixin;
import org.tbee.sway.support.SwayUtil;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.function.Consumer;

public class SComboBox<T> extends JComboBox<T> implements
        JComponentMixin<SComboBox<T>>,
        ExceptionHandlerMixin<SComboBox<T>>,
        DataMixin<SComboBox<T>, T>,
        ValueMixin<SComboBox<T>, T>,
        BindToMixin<SComboBox<T>, T> {

    final static private org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SComboBox.class);

    public SComboBox() {
        setRenderer(new DefaultListCellRenderer(() -> format, () -> alternateRowColor, () -> firstAlternateRowColor, () -> secondAlternateRowColor));
        addActionListener (e -> fireValueChanged());
    }

    // ===========================================================================================================================
    // For Mixins

    @Override
    public BindingEndpoint<T> defaultBindingEndpoint() {
        return value$();
    }


    // =======================================================================
    // DATA

    private List<T> data = List.of();

    /**
     *
     * @param v
     */
    public void setData(List<T> v) {
        firePropertyChange(DATA, this.data, this.data = Collections.unmodifiableList(v)); // We don't allow outside changes to the provided list
        setModel(new DefaultComboBoxModel<>(new Vector<>(this.data)));
    }
    public List<T> getData() {
        return this.data;
    }

    // ===========================================================================
    // RENDERING

    private Format<T> format = null;

    // TBEERNOT not sure how to do this, but I know this API needs to be there. TTD? :-D
    /**
     *
     * @param v
     * @return
     */
    public SComboBox<T> render(Format<T> v) {
        this.format = v;
        return this;
    }

    // TBEERNOT not sure how to do this, but I know this API needs to be there. TTD? :-D
    /**
     *
     * @param clazz
     * @return
     */
    public SComboBox<T> renderFor(Class<T> clazz) {
        return render((Format<T>) SFormatRegistry.findFor(clazz));
    }

    // ===========================================================================
    // ALTERNATE ROW COLORS

    /** Alternate the background color for rows */
    public void setAlternateRowColor(boolean v) {
        firePropertyChange(ALTERNATEROWCOLOR, this.alternateRowColor, this.alternateRowColor = v);
    }
    public boolean getAlternateRowColor() {
        return alternateRowColor;
    }
    private boolean alternateRowColor = true;
    final static public String ALTERNATEROWCOLOR = "alternateRowColor";
    public SComboBox<T> alternateRowColor(boolean v) {
        setAlternateRowColor(v);
        return this;
    }
    public BindingEndpoint<Boolean> alternateRowColor$() {
        return BindingEndpoint.of(this, ALTERNATEROWCOLOR, exceptionHandler);
    }

    /** The color to use for the alternating background color for rows */
    public void setFirstAlternateRowColor(Color v) {
        firePropertyChange(FIRSTALTERNATEROWCOLOR, this.firstAlternateRowColor, this.firstAlternateRowColor = v);
    }
    public Color getFirstAlternateRowColor() {
        return firstAlternateRowColor;
    }
    private Color firstAlternateRowColor = SwayUtil.getFirstAlternateRowColor();
    final static public String FIRSTALTERNATEROWCOLOR = "firstAlternateRowColor";
    public SComboBox<T> firstAlternateRowColor(Color v) {
        firstAlternateRowColor(v);
        return this;
    }
    public BindingEndpoint<Color> firstAlternateRowColor$() {
        return BindingEndpoint.of(this, FIRSTALTERNATEROWCOLOR, exceptionHandler);
    }

    /** The second color to use for the alternating background color for rows */
    public void setSecondAlternateRowColor(Color v) {
        firePropertyChange(SECONDALTERNATEROWCOLOR, this.secondAlternateRowColor, this.secondAlternateRowColor = v);
    }
    public Color getSecondAlternateRowColor() {
        return secondAlternateRowColor;
    }
    private Color secondAlternateRowColor = SwayUtil.getSecondAlternateRowColor();
    final static public String SECONDALTERNATEROWCOLOR = "secondAlternateRowColor";
    public SComboBox<T> secondAlternateRowColor(Color v) {
        setSecondAlternateRowColor(v);
        return this;
    }
    public BindingEndpoint<Color> secondAlternateRowColor$() {
        return BindingEndpoint.of(this, SECONDALTERNATEROWCOLOR, exceptionHandler);
    }


    // ===========================================================================
    // SELECTION

    /**
     *
     * @return
     */
    public T getValue() {
        return (T)getSelectedItem();
    }

    /**
     *
     */
    public void setValue(T v) {
        setSelectedItem(v);
    }

    @Override
    public void setSelectedItem(Object object) {
        Object oldValue = getSelectedItem();
        super.setSelectedItem(object);
        firePropertyChange(VALUE, oldValue, object);
    }

    /**
     *
     * @param listener
     */
    synchronized public void addValueChangedListener(Consumer<T> listener) {
        if (valueChangedListeners == null) {
            valueChangedListeners = new ArrayList<>();
        }
        valueChangedListeners.add(listener);
    }
    synchronized public boolean removeValueChangedListener(Consumer<T> listener) {
        if (valueChangedListeners == null) {
            return false;
        }
        return valueChangedListeners.remove(listener);
    }
    private List<Consumer<T>> valueChangedListeners;
    private void fireValueChanged() {
        if (valueChangedListeners != null) {
            valueChangedListeners.forEach(scl -> scl.accept(getValue()));
        }
    }

    /**
     * @param listener
     * @return
     */
    public SComboBox<T> onValueChanged(Consumer<T> listener) {
        addValueChangedListener(listener);
        return this;
    }


    // ========================================================
    // OF

    static public <T> SComboBox<T> of() {
        return new SComboBox<T>();
    }

    static public <T> SComboBox<T> of(Format<T> format) {
        return new SComboBox<T>().render(format);
    }

    static public <T> SComboBox<T> of(Class<T> clazz) {
        Format<T> format = (Format<T>) SFormatRegistry.findFor(clazz);
        if (format == null) {
            throw new IllegalArgumentException("No format found for " + clazz);
        }
        return of(format);
    }

    static public <T> SComboBox<T> of(List<T> data) {
        return new SComboBox<T>().data(data);
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


    // ===========================================================================
    // FLUENT API

    public SComboBox<T> editable(boolean v) {
        setEditable(v);
        return this;
    }
}

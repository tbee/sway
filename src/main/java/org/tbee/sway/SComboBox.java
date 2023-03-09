package org.tbee.sway;

import org.tbee.sway.binding.BeanBinder;
import org.tbee.sway.binding.BindUtil;
import org.tbee.sway.binding.Binding;
import org.tbee.sway.binding.ExceptionHandler;
import org.tbee.sway.format.Format;
import org.tbee.sway.format.FormatRegistry;
import org.tbee.sway.list.DefaultListCellRenderer;
import org.tbee.sway.support.SwayUtil;
import org.tbee.util.ExceptionUtil;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.function.Consumer;

public class SComboBox<T> extends JComboBox<T> {

    final static private org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SComboBox.class);

    public SComboBox() {
        setRenderer(new DefaultListCellRenderer(() -> format, () -> alternateRowColor, () -> firstAlternateRowColor, () -> secondAlternateRowColor));
        addActionListener (e -> fireValueChanged());
    }


    // =======================================================================
    // DATA

    private List<T> data = List.of();

    /**
     *
     * @param v
     */
    public void setData(List<T> v) {
// TBEERNOT        unregisterFromAllBeans();
        this.data = Collections.unmodifiableList(v); // We don't allow outside changes to the provided list
        setModel(new DefaultComboBoxModel<>(new Vector<>(this.data)));
// TBEERNOT       registerToAllBeans();
    }
    public List<T> getData() {
        return this.data;
    }
    public SComboBox<T> data(List<T> v) {
        setData(v);
        return this;
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
        return render((Format<T>) FormatRegistry.findFor(clazz));
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
    static final public String VALUE = "value";

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
        Format<T> format = (Format<T>) FormatRegistry.findFor(clazz);
        if (format == null) {
            throw new IllegalArgumentException("No format found for " + clazz);
        }
        return of(format);
    }

    static public <T> SComboBox<T> of(List<T> data) {
        return new SComboBox<T>().data(data);
    }


    // ===========================================================================
    // FLUENT API

    public SComboBox<T> name(String v) {
        setName(v);
        return this;
    }

    public SComboBox<T> visible(boolean v) {
        setVisible(v);
        return this;
    }

    public SComboBox<T> value(T v) {
        setValue(v);
        return this;
    }


    // ========================================================
    // BIND

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
    public SComboBox<T> exceptionHandler(ExceptionHandler v) {
        setExceptionHandler(v);
        return this;
    }
    final static public String EXCEPTIONHANDLER = "exceptionHandler";
    ExceptionHandler exceptionHandler = this::handleException;

    private boolean handleException(Throwable e, JComponent component, Object oldValue, Object newValue) {
        return handleException(e);
    }
    private boolean handleException(Throwable e) {

        // Force focus back
        SwingUtilities.invokeLater(() -> this.grabFocus());

        // Display the error
        if (LOGGER.isDebugEnabled()) LOGGER.debug(e.getMessage(), e);
        JOptionPane.showMessageDialog(this, ExceptionUtil.determineMessage(e), "ERROR", JOptionPane.ERROR_MESSAGE);

        // Mark exception as handled
        return true;
    }

    /**
     * Will create a binding to a specific bean/property.
     * Use binding(BeanBinding, PropertyName) to be able to switch beans while keeping the bind.
     *
     * @param bean
     * @param propertyName
     * @return Binding, so unbind() can be called
     */
    public Binding binding(Object bean, String propertyName) {
        return BindUtil.bind(this, VALUE, bean, propertyName, exceptionHandler);
    }

    /**
     * Will create a binding to a specific bean/property.
     * Use bind(BeanBinding, PropertyName) to be able to switch beans while keeping the bind.
     *
     * @param bean
     * @param propertyName
     * @return this, for fluent API
     */
    public SComboBox<T> bind(Object bean, String propertyName) {
        binding(bean, propertyName);
        return this;
    }

    /**
     * Bind to a bean wrapper's property.
     * This will allow the swap the bean (in the BeanBinder) without having to rebind.
     *
     * @param beanBinder
     * @param propertyName
     * @return Binding, so unbind() can be called
     */
    public Binding binding(BeanBinder<?> beanBinder, String propertyName) {
        return BindUtil.bind(this, VALUE, beanBinder, propertyName, exceptionHandler);
    }

    /**
     * Bind to a bean wrapper's property.
     * This will allow the swap the bean (in the BeanBinder) without having to rebind.
     * @param beanBinder
     * @param propertyName
     * @return this, for fluent API
     */
    public SComboBox<T> bind(BeanBinder<?> beanBinder, String propertyName) {
        binding(beanBinder, propertyName);
        return this;
    }

    // TBEERNOT Tests
}

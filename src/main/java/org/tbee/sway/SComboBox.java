package org.tbee.sway;

import org.tbee.sway.binding.BeanBinder;
import org.tbee.sway.binding.BindingEndpoint;
import org.tbee.sway.binding.ExceptionHandler;
import org.tbee.sway.format.Format;
import org.tbee.sway.format.FormatRegistry;
import org.tbee.sway.list.DefaultListCellRenderer;
import org.tbee.sway.mixin.PropertyChangeListenerMixin;
import org.tbee.sway.support.SwayUtil;
import org.tbee.util.ExceptionUtil;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Component;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.function.Consumer;

public class SComboBox<T> extends JComboBox<T>implements PropertyChangeListenerMixin<SComboBox<T>> {

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
    static final public String VALUE = "value";

    public SComboBox<T> value(T v) {
        setValue(v);
        return this;
    }
    public BindingEndpoint<T> value$() {
        return BindingEndpoint.of(this, VALUE, exceptionHandler);
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
        Format<T> format = (Format<T>) FormatRegistry.findFor(clazz);
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
    public SComboBox<T> exceptionHandler(ExceptionHandler v) {
        setExceptionHandler(v);
        return this;
    }
    final static public String EXCEPTIONHANDLER = "exceptionHandler";
    ExceptionHandler exceptionHandler = this::handleException;
    public BindingEndpoint<ExceptionHandler> exceptionHandler$() {
        return BindingEndpoint.of(this, EXCEPTIONHANDLER, exceptionHandler);
    }

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

    public SComboBox<T> withPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        super.addPropertyChangeListener(propertyName, listener);
        return this;
    }
    public SComboBox<T> withPropertyChangeListener(PropertyChangeListener listener) {
        super.addPropertyChangeListener(listener);
        return this;
    }

    public SComboBox<T> overlayWith(Component overlayComponent) {
        SOverlayPane.overlayWith(this, overlayComponent);
        return this;
    }
    public SComboBox<T> removeOverlay(Component overlayComponent) {
        SOverlayPane.removeOverlay(this, overlayComponent);
        return this;
    }

    /**
     * Binds to the default property 'value'
     */
    public SComboBox<T> bindTo(BindingEndpoint<T> bindingEndpoint) {
        value$().bindTo(bindingEndpoint);
        return this;
    }

    /**
     * Binds to the default property 'value'.
     * Binding in this way is not type safe!
     */
    public SComboBox<T> bindTo(Object bean, String propertyName) {
        return bindTo(BindingEndpoint.of(bean, propertyName));
    }

    /**
     * Binds to the default property 'value'.
     * Binding in this way is not type safe!
     */
    public SComboBox<T> bindTo(BeanBinder<?> beanBinder, String propertyName) {
        return bindTo(BindingEndpoint.of(beanBinder, propertyName));
    }

    // TBEERNOT Tests
}

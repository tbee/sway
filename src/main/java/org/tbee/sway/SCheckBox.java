package org.tbee.sway;

import org.tbee.sway.binding.BeanBinder;
import org.tbee.sway.binding.BindUtil;
import org.tbee.sway.binding.Binding;
import org.tbee.util.ExceptionUtil;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class SCheckBox extends JCheckBox {
    static private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SCheckBox.class);

    public SCheckBox() {
    }

    public SCheckBox(Icon icon) {
        super(icon);
    }

    public SCheckBox(String text) {
        super(text);
    }

    public SCheckBox(String text, Icon icon) {
        super(text, icon);
    }

    // ==================================================
    // PROPERTIES

    @Override
    protected void fireStateChanged() {
        super.fireStateChanged();
        firePropertyChange(SELECTED, selected, selected = isSelected());
    }
    final static public String SELECTED = "selected";
    boolean selected = false;
    public SCheckBox selected(boolean value) {
        setSelected(value);
        return this;
    }

    // ==================================================
    // BIND

    /**
     * Will create a binding to a specific bean/property.
     * Use binding(BeanBinding, PropertyName) to be able to switch beans while keeping the bind.
     *
     * @param bean
     * @param propertyName
     * @return Binding, so unbind() can be called
     */
    public Binding binding(Object bean, String propertyName) {
        return BindUtil.bind(this, SELECTED, bean, propertyName, this::handleException);
    }

    /**
     * Will create a binding to a specific bean/property.
     * Use bind(BeanBinding, PropertyName) to be able to switch beans while keeping the bind.
     *
     * @param bean
     * @param propertyName
     * @return this, for fluent API
     */
    public SCheckBox bind(Object bean, String propertyName) {
        Binding binding = binding(bean, propertyName);
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
    public Binding binding(BeanBinder beanBinder, String propertyName) {
        return BindUtil.bind(this, SELECTED, beanBinder, propertyName, this::handleException);
    }

    /**
     * Bind to a bean wrapper's property.
     * This will allow the swap the bean (in the BeanBinder) without having to rebind.
     * @param beanBinder
     * @param propertyName
     * @return this, for fluent API
     */
    public SCheckBox bind(BeanBinder beanBinder, String propertyName) {
        Binding binding = binding(beanBinder, propertyName);
        return this;
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
}

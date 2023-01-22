package org.tbee.sway;

import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.tbee.sway.binding.BeanBinder;
import org.tbee.sway.binding.BindUtil;
import org.tbee.sway.binding.Binding;
import org.tbee.sway.support.IconRegistry;
import org.tbee.util.ExceptionUtil;

/**
 * If the SELECTED, UNSELECTED icons are specified in the IconRegistry, then these will be drawn.
 */
public class SCheckBox extends JCheckBox {
    static private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SCheckBox.class);

    public SCheckBox() {
        updateIcon();
    }

    public SCheckBox(Icon icon) {
        super(icon);
        explicitIcon = true;
    }

    public SCheckBox(String text) {
        super(text);
        updateIcon();
    }

    public SCheckBox(String text, Icon icon) {
        super(text, icon);
        explicitIcon = true;
    }

    private boolean explicitIcon = false;

    // ==================================================
    // PROPERTIES

    @Override
    protected void fireStateChanged() {
        super.fireStateChanged();
        firePropertyChange(SELECTED, selected, selected = isSelected());
        updateIcon();
    }

    private void updateIcon() {
        if (explicitIcon) {
            return;
        }
        super.setIcon(IconRegistry.find(selected ? IconRegistry.SwayInternallyUsedIcon.CHECKBOX_SELECTED : IconRegistry.SwayInternallyUsedIcon.CHECKBOX_UNSELECTED));
    }

    final static public String SELECTED = "selected";
    boolean selected = false;
    public SCheckBox selected(boolean value) {
        setSelected(value);
        return this;
    }

    @Override
    public void setIcon(Icon v) {
        super.setIcon(v);
        explicitIcon = true;
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

    // ==============================================
    // FLUENT API

    public SCheckBox name(String v) {
        setName(v);
        return this;
    }

    public SCheckBox toolTipText(String t) {
        super.setToolTipText(t);
        return this;
    }

    public SCheckBox enabled(boolean v) {
        super.setEnabled(v);
        return this;
    }

    public SCheckBox margin(Insets m) {
        super.setMargin(m);
        return this;
    }

    public SCheckBox onAction(ActionListener l) {
        super.addActionListener(l);
        return this;
    }

    public SCheckBox action(Action v) {
        super.setAction(v);
        return this;
    }

    public SCheckBox icon(Icon v) {
        super.setIcon(v);
        return this;
    }

    public SCheckBox text(String v) {
        super.setText(v);
        return this;
    }

    public SCheckBox visible(boolean value) {
        setVisible(value);
        return this;
    }
}

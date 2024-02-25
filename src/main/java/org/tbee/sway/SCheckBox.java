package org.tbee.sway;

import org.tbee.sway.binding.BeanBinder;
import org.tbee.sway.binding.BindingEndpoint;
import org.tbee.sway.binding.ExceptionHandler;
import org.tbee.sway.mixin.ComponentMixin;
import org.tbee.sway.support.IconRegistry;
import org.tbee.util.ExceptionUtil;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.Insets;
import java.awt.event.ActionListener;

/**
 * If the SELECTED, UNSELECTED icons are specified in the IconRegistry, then these will be drawn.
 */
public class SCheckBox extends JCheckBox implements
        ComponentMixin<SCheckBox> {
    static private org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SCheckBox.class);

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
    public BindingEndpoint<Boolean> selected$() {
        return BindingEndpoint.of(this, SELECTED, exceptionHandler);
    }

    @Override
    public void setIcon(Icon v) {
        super.setIcon(v);
        explicitIcon = true;
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
    public SCheckBox exceptionHandler(ExceptionHandler v) {
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


    // ==============================================
    // FLUENT API

    static public SCheckBox of() {
        return new SCheckBox();
    }

    static public SCheckBox of(String text) {
        return of().text(text);
    }

    static public SCheckBox of(Icon icon) {
        return of().icon(icon);
    }

    static public SCheckBox of(String text, Icon icon) {
        return of().text(text).icon(icon);
    }

    public SCheckBox toolTipText(String t) {
        setToolTipText(t);
        return this;
    }

    public SCheckBox margin(Insets m) {
        setMargin(m);
        return this;
    }

    public SCheckBox onAction(ActionListener l) {
        addActionListener(l);
        return this;
    }

    public SCheckBox action(Action v) {
        setAction(v);
        return this;
    }

    public SCheckBox icon(Icon v) {
        setIcon(v);
        return this;
    }

    public SCheckBox text(String v) {
        setText(v);
        return this;
    }

    /**
     * Binds the default property 'selected'
     */
    public SCheckBox bindTo(BindingEndpoint<Boolean> bindingEndpoint) {
        selected$().bindTo(bindingEndpoint);
        return this;
    }

    /**
     * Binds to the default property 'selected'.
     * Binding in this way is not type safe!
     */
    public SCheckBox bindTo(Object bean, String propertyName) {
        return bindTo(BindingEndpoint.of(bean, propertyName));
    }

    /**
     * Binds to the default property 'selected'.
     * Binding in this way is not type safe!
     */
    public SCheckBox bindTo(BeanBinder<?> beanBinder, String propertyName) {
        return bindTo(BindingEndpoint.of(beanBinder, propertyName));
    }
}

package org.tbee.sway;

import org.tbee.sway.binding.BindingEndpoint;
import org.tbee.sway.binding.ExceptionHandler;
import org.tbee.sway.mixin.ActionMixin;
import org.tbee.sway.mixin.BindToMixin;
import org.tbee.sway.mixin.ComponentMixin;
import org.tbee.sway.mixin.ExceptionHandlerMixin;
import org.tbee.sway.mixin.MarginMixin;
import org.tbee.sway.mixin.SelectedMixin;
import org.tbee.sway.mixin.TextIconMixin;
import org.tbee.sway.mixin.ToolTipMixin;
import org.tbee.sway.support.IconRegistry;

import javax.swing.Icon;
import javax.swing.JCheckBox;

/**
 * If the SELECTED, UNSELECTED icons are specified in the IconRegistry, then these will be drawn.
 */
public class SCheckBox extends JCheckBox implements
        ComponentMixin<SCheckBox>,
        TextIconMixin<SCheckBox>,
        ActionMixin<SCheckBox>,
        ToolTipMixin<SCheckBox>,
        MarginMixin<SCheckBox>,
        SelectedMixin<SCheckBox>,
        ExceptionHandlerMixin<SCheckBox>,
        BindToMixin<SCheckBox, Boolean> {
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

    // ===========================================================================================================================
    // For Mixins

    @Override
    public BindingEndpoint<Boolean> defaultBindingEndpoint() {
        return selected$();
    }

    // ==================================================
    // PROPERTIES

    @Override
    protected void fireStateChanged() {
        super.fireStateChanged();
        firePropertyChange(SELECTED, selected, selected = isSelected());
        updateIcon();
    }
    boolean selected = false;

    private void updateIcon() {
        if (explicitIcon) {
            return;
        }
        super.setIcon(IconRegistry.find(selected ? IconRegistry.SwayInternallyUsedIcon.CHECKBOX_SELECTED : IconRegistry.SwayInternallyUsedIcon.CHECKBOX_UNSELECTED));
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
    private ExceptionHandler exceptionHandler = this::handleException;


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
}

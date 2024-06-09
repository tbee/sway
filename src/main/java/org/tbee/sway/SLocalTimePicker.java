package org.tbee.sway;

import net.miginfocom.layout.AlignX;
import org.tbee.sway.binding.ExceptionHandler;
import org.tbee.sway.mixin.ExceptionHandlerDefaultMixin;
import org.tbee.sway.mixin.ValueMixin;
import org.tbee.sway.support.HAlign;

import javax.swing.JLabel;
import java.beans.PropertyVetoException;
import java.time.LocalTime;

import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.TIMEPICKER_NEXTHOUR;
import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.TIMEPICKER_NEXTMINUTE;
import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.TIMEPICKER_NEXTSECOND;
import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.TIMEPICKER_PREVHOUR;
import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.TIMEPICKER_PREVMINUTE;
import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.TIMEPICKER_PREVSECOND;

public class SLocalTimePicker extends SMigPanel implements
        ValueMixin<SLocalTimePicker, LocalTime>,
        ExceptionHandlerDefaultMixin<SLocalTimePicker> {

    private final STextField<Integer> hourTextField = STextField.ofInteger()
            .transparentAsLabel()
            .hAlign(HAlign.CENTER);
    private final STextField<Integer> minuteTextField = STextField.ofInteger()
            .transparentAsLabel()
            .hAlign(HAlign.CENTER);
    private final STextField<Integer> secondTextField = STextField.ofInteger()
            .transparentAsLabel()
            .hAlign(HAlign.CENTER);

    // ===========================================================================================================
    // CONSTRUCTOR

    public SLocalTimePicker() {
        this(LocalTime.now());
    }

    public SLocalTimePicker(LocalTime localTime) {

        // setup defaults
        value(localTime);

        // layout
        addComponent(iconButton(TIMEPICKER_NEXTHOUR, this::nextHour)).alignX(AlignX.CENTER);
        addComponent(iconButton(TIMEPICKER_NEXTMINUTE, this::nextMinute)).alignX(AlignX.CENTER).skip();
        addComponent(iconButton(TIMEPICKER_NEXTSECOND, this::nextSecond)).alignX(AlignX.CENTER).skip();
        wrap();
        addComponent(hourTextField).sizeGroup("time").alignX(AlignX.CENTER).growX();
        addComponent(new JLabel(":")).sizeGroup("sep").alignX(AlignX.CENTER);
        addComponent(minuteTextField).sizeGroup("time").alignX(AlignX.CENTER).growX();
        addComponent(new JLabel(":")).sizeGroup("sep").alignX(AlignX.CENTER);
        addComponent(secondTextField).sizeGroup("time").alignX(AlignX.CENTER).growX();
        wrap();
        addComponent(iconButton(TIMEPICKER_PREVHOUR, this::prevHour)).alignX(AlignX.CENTER);
        addComponent(iconButton(TIMEPICKER_PREVMINUTE, this::prevMinute)).alignX(AlignX.CENTER).skip();
        addComponent(iconButton(TIMEPICKER_PREVSECOND, this::prevSecond)).alignX(AlignX.CENTER).skip();

        updateComponents();
    }

    private SButton iconButton(SIconRegistry.SwayInternallyUsedIcon icon, Runnable runnable) {
        return SButton.of(SIconRegistry.find(icon))
                .asImageButton()
                .onAction(e -> runnable.run());
    }

    private int decrease(int value, int max) {
        return (value <= 0 ? max : value - 1);
    }
    private int increase(int value, int max) {
        return (value >= max ? 0 : value + 1);
    }

    private void prevHour() {
        value = value.withHour(decrease(value.getHour(), 23));
        updateComponents();
    }

    private void nextHour() {
        value = value.withHour(increase(value.getHour(), 23));
        updateComponents();
    }

    private void prevMinute() {
        value = value.withMinute(decrease(value.getMinute(), 59));
        updateComponents();
    }

    private void nextMinute() {
        value = value.withMinute(increase(value.getMinute(), 59));
        updateComponents();
    }

    private void prevSecond() {
        value = value.withSecond(decrease(value.getMinute(), 59));
        updateComponents();
    }

    private void nextSecond() {
        value = value.withSecond(increase(value.getMinute(), 59));
        updateComponents();
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

    // ===========================================================================================================
    // PROPERTIES

    public LocalTime getValue() {
        return value;
    }
    public void setValue(LocalTime v) {
        try {
            // update calendar
            fireVetoableChange(VALUE, this.value, v);
            firePropertyChange(VALUE, this.value, this.value = v);
        }
        catch (PropertyVetoException e) {
            throw new IllegalArgumentException(e);
        }
    }
    private LocalTime value = null;


    // ===========================================================================================================
    // LAYOUT

    protected void updateComponents() {

        // TODO: AM/PM
        hourTextField.setValue(value.getHour());
        minuteTextField.setValue(value.getMinute());
        secondTextField.setValue(value.getSecond());
    }


    // =============================================================================
    // SUPPORT


    static public SLocalTimePicker of() {
        return new SLocalTimePicker();
    }
}

package org.tbee.sway;

import net.miginfocom.layout.AlignX;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;
import org.tbee.sway.binding.ExceptionHandler;
import org.tbee.sway.mixin.ExceptionHandlerMixin;
import org.tbee.sway.mixin.JComponentMixin;
import org.tbee.sway.mixin.ValueMixin;

import javax.swing.JPanel;
import java.beans.PropertyVetoException;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Objects;

import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.ZONEPICKER_PLUS;

public class SZoneOffsetPicker extends JPanel implements
        ValueMixin<SZoneOffsetPicker, ZoneOffset>,
        ExceptionHandlerMixin<SZoneOffsetPicker>,
        JComponentMixin<SZoneOffsetPicker> {

    private final SLocalTimePicker timePicker = new SLocalTimePicker(null, () -> LocalTime.of(0, 0));

    // ===========================================================================================================
    // CONSTRUCTOR

    public SZoneOffsetPicker() {
        this(null);
    }

    public SZoneOffsetPicker(ZoneOffset zoneOffset) {

        // setup defaults
        setValue(zoneOffset);

        // layout
        setLayout(new MigLayout(new LC().gridGap("0", "0").insets("0")));
        add(SLabel.of(SIconRegistry.find(ZONEPICKER_PLUS)), new CC().alignX(AlignX.RIGHT).gapX("0", "0"));
        add(timePicker, new CC().alignX(AlignX.LEFT));
        timePicker.setShowSeconds(false);

        // Adopt changes
        timePicker.value$().onChange(v -> deriveValue());
    }

    private void deriveValue() {
        if (derivingValue > 0) {
            return;
        }
        derivingValue++;

        try {
            LocalTime localTime = timePicker.getValue();
            setValue(localTime == null ? null : ZoneOffset.ofHoursMinutesSeconds(localTime.getHour(), localTime.getMinute(), localTime.getSecond()));
        }
        finally {
            derivingValue--;
        }
    }
    private int derivingValue = 0; // this all happens in the EDT thead, so no need for multithreading fuss

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

    public ZoneOffset getValue() {
        return value;
    }
    public void setValue(ZoneOffset v) {
        try {
            boolean changed = !Objects.equals(this.value, v);

            fireVetoableChange(VALUE, this.value, v);
            firePropertyChange(VALUE, this.value, this.value = v);

            if (changed) {
                timePicker.setValue(v == null ? null : toLocalTime(v));
            }
        }
        catch (PropertyVetoException e) {
            throw new IllegalArgumentException(e);
        }
    }
    private ZoneOffset value = null;

    private LocalTime toLocalTime(ZoneOffset zoneOffset) {
        int seconds = zoneOffset.getTotalSeconds();
        int hours = seconds / 60 / 60;
        seconds -= hours * 60 * 60;
        int minutes = seconds / 60;
        seconds -= minutes * 60;
        return LocalTime.of(hours, minutes, seconds);
    }

    public boolean getShowSeconds() {
        return showSeconds;
    }
    public void setShowSeconds(boolean v) {
        try {
            fireVetoableChange(SHOWSECONDS, this.showSeconds, v);
            firePropertyChange(SHOWSECONDS, this.showSeconds, this.showSeconds = v);
            timePicker.setShowSeconds(v);
        }
        catch (PropertyVetoException e) {
            throw new IllegalArgumentException(e);
        }
    }
    private boolean showSeconds = timePicker.getShowSeconds();
    public static String SHOWSECONDS = "showSeconds";
    public SZoneOffsetPicker showSeconds(boolean v) {
        setShowSeconds(v);
        return this;
    }

    public boolean getShowClear() {
        return showClear;
    }
    public void setShowClear(boolean v) {
        try {
            fireVetoableChange(SHOWCLEAR, this.showClear, v);
            firePropertyChange(SHOWCLEAR, this.showClear, this.showClear = v);
            timePicker.setShowClear(v);
        }
        catch (PropertyVetoException e) {
            throw new IllegalArgumentException(e);
        }
    }
    private boolean showClear = true;
    public static String SHOWCLEAR = "showClear";
    public SZoneOffsetPicker showClear(boolean v) {
        setShowClear(v);
        return this;
    }

    // ===========================================================================================================
    // LAYOUT


    // =============================================================================
    // FLUENT API

    static public SZoneOffsetPicker of() {
        return new SZoneOffsetPicker();
    }
}

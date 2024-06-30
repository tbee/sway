package org.tbee.sway;

import net.miginfocom.layout.AlignX;
import net.miginfocom.layout.CC;
import org.tbee.sway.binding.ExceptionHandler;
import org.tbee.sway.mixin.ExceptionHandlerMixin;
import org.tbee.sway.mixin.JComponentMixin;
import org.tbee.sway.mixin.ValueMixin;

import javax.swing.Icon;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.beans.PropertyVetoException;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Objects;

import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.ZONEPICKER_MINUS;
import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.ZONEPICKER_PLUS;
import static org.tbee.sway.SLocalTimePicker.DATA_ROW;
import static org.tbee.sway.SLocalTimePicker.HOUR_COL;

public class SZoneOffsetPicker extends JPanel implements
        ValueMixin<SZoneOffsetPicker, ZoneOffset>,
        ExceptionHandlerMixin<SZoneOffsetPicker>,
        JComponentMixin<SZoneOffsetPicker> {

    public final Icon minusIcon = SIconRegistry.find(ZONEPICKER_MINUS);
    public final Icon plusIcon = SIconRegistry.find(ZONEPICKER_PLUS);
    public final SButton plusminusButton = SButton.of(plusIcon, e -> toggleSign()).asImageButton();

    private final SLocalTimePicker timePicker = new SLocalTimePicker(null, () -> LocalTime.of(0, 0)) {
        @Override
        int modify(int value, int delta, int max) {
            int sign = (plusminusButton.getIcon() == minusIcon ? -1 : 1);
            max = (max == HOURS_MAX ? 18 : max); // hours can only go to +/-18:00 (TODO: this is a bit of a kludge, think of something more correct)

            value += sign * delta;
            while (value > max) {
                value = max;
            }
            while (value < 0) {
                toggleSign();
                value = -value;
            }
            return value;
        }
    };

    // ===========================================================================================================
    // CONSTRUCTOR

    public SZoneOffsetPicker() {
        this(null);
    }

    public SZoneOffsetPicker(ZoneOffset zoneOffset) {

        // setup defaults
        setValue(zoneOffset);

        // layout
        setLayout(new BorderLayout());
        add(timePicker, BorderLayout.CENTER);

        // Setup timepicker
        timePicker.migPanel.add(plusminusButton, new CC().cell(HOUR_COL - 1, DATA_ROW).alignX(AlignX.RIGHT));
        timePicker.setShowSeconds(false);

        // Adopt changes
        timePicker.value$().onChange(v -> deriveValue());
    }


    private void toggleSign() {
        plusminusButton.setIcon(plusminusButton.getIcon().equals(minusIcon) ? plusIcon : minusIcon);
        deriveValue();
    }

    private void deriveValue() {
        if (derivingValue > 0) {
            return;
        }
        derivingValue++;

        try {
            LocalTime localTime = timePicker.getValue();
            if (localTime == null) {
                setValue(null);
            }
            else {
                int hours = localTime.getHour();
                int minutes = localTime.getMinute();
                int seconds = localTime.getSecond();
                int sign = sign();
                setValue(ZoneOffset.ofTotalSeconds(sign * ((hours * 60 * 60) + (minutes * 60) + seconds)));
            }
        }
        finally {
            derivingValue--;
        }
    }

    private int sign() {
        return plusminusButton.getIcon().equals(minusIcon) ? -1 : 1;
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
                int seconds = v.getTotalSeconds();
                timePicker.setValue(v == null ? null : toLocalTime(seconds));
                plusminusButton.setIcon(seconds < 0 ? minusIcon : plusIcon);
            }
        }
        catch (PropertyVetoException e) {
            throw new IllegalArgumentException(e);
        }
    }
    private ZoneOffset value = null;

    private LocalTime toLocalTime(int seconds) {
        seconds = Math.abs(seconds);
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

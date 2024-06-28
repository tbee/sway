package org.tbee.sway;

import net.miginfocom.layout.AlignX;
import net.miginfocom.layout.HideMode;
import org.tbee.sway.binding.ExceptionHandler;
import org.tbee.sway.format.IntegerFormat;
import org.tbee.sway.mixin.ExceptionHandlerMixin;
import org.tbee.sway.mixin.JComponentMixin;
import org.tbee.sway.mixin.ValueMixin;
import org.tbee.sway.support.HAlign;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.beans.PropertyVetoException;
import java.time.LocalTime;
import java.util.function.Supplier;

import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.TIMEPICKER_CLEAR;
import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.TIMEPICKER_NEXTHOUR;
import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.TIMEPICKER_NEXTMINUTE;
import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.TIMEPICKER_NEXTSECOND;
import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.TIMEPICKER_PREVHOUR;
import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.TIMEPICKER_PREVMINUTE;
import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.TIMEPICKER_PREVSECOND;

// TODO: AM/PM
public class SLocalTimePicker extends JPanel implements
        ValueMixin<SLocalTimePicker, LocalTime>,
        ExceptionHandlerMixin<SLocalTimePicker>,
        JComponentMixin<SLocalTimePicker> {

    class TimeFormat extends IntegerFormat {
        public static final String NULL_STRING = "--";

        private final int maxValue;
        public TimeFormat(int maxValue) {
            super("00");
            this.maxValue = maxValue;
        }

        @Override
        public String toString(Integer value) {
            return value == null ? NULL_STRING : super.toString(value);
        }

        @Override
        public Integer toValue(String string) {
            if (string.isEmpty()) {
                return null;
            }
            Integer value = NULL_STRING.equals(string) ? null : super.toValue(string.replaceAll("-", ""));
            if (value != null && value > maxValue) {
                throw new NumberFormatException("Value " + value + " exceeds max value " + maxValue);
            }
            return value;
        }
    };

    private final STextField<Integer> hourTextField = STextField.of(new TimeFormat(23))
            .transparentAsLabel()
            .hAlign(HAlign.CENTER);
    private final SLabel minuteSeparator = SLabel.of(":");
    private final STextField<Integer> minuteTextField = STextField.of(new TimeFormat(59))
            .transparentAsLabel()
            .hAlign(HAlign.CENTER);
    private final SLabel secondSeparator = SLabel.of(":");
    private final STextField<Integer> secondTextField = STextField.of(new TimeFormat(59))
            .transparentAsLabel()
            .hAlign(HAlign.CENTER);

    private final SButton hourDownButton = iconButton(TIMEPICKER_PREVHOUR, () -> modifyHour(-1));
    private final SButton hourUpButton = iconButton(TIMEPICKER_NEXTHOUR, () -> modifyHour(1));
    private final SButton minuteDownButton = iconButton(TIMEPICKER_PREVMINUTE, () -> modifyMinute(-1));
    private final SButton minuteUpButton = iconButton(TIMEPICKER_NEXTMINUTE, () -> modifyMinute(1));
    private final SButton secondDownButton = iconButton(TIMEPICKER_PREVSECOND, () -> modifySecond(-1));
    private final SButton secondUpButton = iconButton(TIMEPICKER_NEXTSECOND, () -> modifySecond(1));
    private final SButton clearButton = iconButton(TIMEPICKER_CLEAR, this::clear);

    private final Supplier<LocalTime> defaultSupplier;
    // ===========================================================================================================
    // CONSTRUCTOR

    public SLocalTimePicker() {
        this(null);
    }

    public SLocalTimePicker(LocalTime localTime) {
        this(localTime, () -> LocalTime.now());
    }

    public SLocalTimePicker(LocalTime localTime, Supplier<LocalTime> defaultSupplier) {
        this.defaultSupplier = defaultSupplier;

        // setup defaults
        value(localTime);
        hourTextField.onFocusLost(e -> this.manualTyped());
        minuteTextField.onFocusLost(e -> this.manualTyped());
        secondTextField.onFocusLost(e -> this.manualTyped());

        // layout
        SMigPanel smigPanel = new SMigPanel().noGaps().noMargins().hideMode(HideMode.DISREGARD);
        smigPanel.addComponent(hourUpButton).alignX(AlignX.CENTER);
        smigPanel.addComponent(minuteUpButton).alignX(AlignX.CENTER).skip();
        smigPanel.addComponent(secondUpButton).alignX(AlignX.CENTER).skip();
        smigPanel.addComponent(SLabel.of()).wrap(); // we cannot attach the wrap to the last component, because it may be hidden
        smigPanel.addComponent(hourTextField).sizeGroup("time").alignX(AlignX.CENTER).growX();
        smigPanel.addComponent(minuteSeparator).sizeGroup("sep").alignX(AlignX.CENTER);
        smigPanel.addComponent(minuteTextField).sizeGroup("time").alignX(AlignX.CENTER).growX();
        smigPanel.addComponent(secondSeparator).sizeGroup("sep").alignX(AlignX.CENTER);
        smigPanel.addComponent(secondTextField).sizeGroup("time").alignX(AlignX.CENTER).growX();
        smigPanel.addComponent(clearButton).alignX(AlignX.LEFT);
        smigPanel.addComponent(SLabel.of()).wrap(); // we cannot attach the wrap to the last component, because it may be hidden
        smigPanel.addComponent(hourDownButton).alignX(AlignX.CENTER);
        smigPanel.addComponent(minuteDownButton).alignX(AlignX.CENTER).skip();
        smigPanel.addComponent(secondDownButton).alignX(AlignX.CENTER).skip();

        setLayout(new BorderLayout());
        add(smigPanel, BorderLayout.CENTER);

        updateComponents();
    }

    private SButton iconButton(SIconRegistry.SwayInternallyUsedIcon icon, Runnable runnable) {
        return SButton.of(SIconRegistry.find(icon))
                .asImageButton()
                .onAction(e -> runnable.run());
    }

    private LocalTime unnull(final LocalTime value) {
        if (value != null) {
            return value;
        }
        LocalTime result = defaultSupplier.get().withNano(0);
        if (!showSeconds) {
            result = result.withSecond(0);
        }
        return result;
    }

    private void manualTyped() {
        // If there is a value, but one part becomes null, all becomes null
        if (value != null && (hourTextField.getValue() == null || minuteTextField.getValue() == null || secondTextField.getValue() == null)) {
            value = null;
        }
        else {
            value = unnull(value);
            value.withHour(hourTextField.getValue() != null ? hourTextField.getValue() : value.getHour())
                 .withMinute(minuteTextField.getValue() != null ? minuteTextField.getValue() : value.getMinute())
                 .withSecond(secondTextField.getValue() != null ? secondTextField.getValue() : value.getSecond());
        }
        setValue(value);
        updateComponents();
    }

    private void modifyHour(int delta) {
        value = unnull(value);
        value(value.withHour(modify(value.getHour(), delta, 23)));
    }

    private void modifyMinute(int delta) {
        value = unnull(value);
        value(value.withMinute(modify(value.getMinute(), delta, 59)));
    }

    private void modifySecond(int delta) {
        value = unnull(value);
        value(value.withSecond(modify(value.getSecond(), delta, 59)));
    }

    private int modify(int value, int delta, int max) {
        value += delta;
        while (value > max) {
            value -= (max + 1);
        }
        while (value < 0) {
            value += (max + 1);
        }
        return value;
    }

    private void clear() {
        value(null);
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
            fireVetoableChange(VALUE, this.value, v);
            firePropertyChange(VALUE, this.value, this.value = v);
            updateComponents();
        }
        catch (PropertyVetoException e) {
            throw new IllegalArgumentException(e);
        }
    }
    private LocalTime value = null;

    public boolean getShowSeconds() {
        return showSeconds;
    }
    public void setShowSeconds(boolean v) {
        try {
            fireVetoableChange(SHOWSECONDS, this.showSeconds, v);
            firePropertyChange(SHOWSECONDS, this.showSeconds, this.showSeconds = v);
            updateComponents();
        }
        catch (PropertyVetoException e) {
            throw new IllegalArgumentException(e);
        }
    }
    private boolean showSeconds = true;
    public static String SHOWSECONDS = "showSeconds";
    public SLocalTimePicker showSeconds(boolean v) {
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
            updateComponents();
        }
        catch (PropertyVetoException e) {
            throw new IllegalArgumentException(e);
        }
    }
    private boolean showClear = true;
    public static String SHOWCLEAR = "showClear";
    public SLocalTimePicker showClear(boolean v) {
        setShowClear(v);
        return this;
    }

    // ===========================================================================================================
    // LAYOUT

    protected void updateComponents() {
        hourTextField.setValue(value == null ? null : value.getHour());
        minuteTextField.setValue(value == null ? null : value.getMinute());
        secondTextField.setValue(value == null ? null : value.getSecond());

        secondUpButton.visible(showSeconds);
        secondSeparator.visible(showSeconds);
        secondTextField.visible(showSeconds);
        secondDownButton.visible(showSeconds);

        clearButton.visible(showClear);
    }


    // =============================================================================
    // FLUENT API

    static public SLocalTimePicker of() {
        return new SLocalTimePicker();
    }
}

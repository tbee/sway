package org.tbee.sway;

import net.miginfocom.layout.AlignX;
import org.tbee.sway.binding.ExceptionHandler;
import org.tbee.sway.format.IntegerFormat;
import org.tbee.sway.mixin.ExceptionHandlerMixin;
import org.tbee.sway.mixin.ValueMixin;
import org.tbee.sway.support.HAlign;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.beans.PropertyVetoException;
import java.time.LocalTime;

import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.TIMEPICKER_NEXTHOUR;
import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.TIMEPICKER_NEXTMINUTE;
import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.TIMEPICKER_NEXTSECOND;
import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.TIMEPICKER_PREVHOUR;
import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.TIMEPICKER_PREVMINUTE;
import static org.tbee.sway.SIconRegistry.SwayInternallyUsedIcon.TIMEPICKER_PREVSECOND;

// TODO: AM/PM
public class SLocalTimePicker extends JPanel implements
        ValueMixin<SLocalTimePicker, LocalTime>,
        ExceptionHandlerMixin<SLocalTimePicker> {

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
    private final STextField<Integer> minuteTextField = STextField.of(new TimeFormat(59))
            .transparentAsLabel()
            .hAlign(HAlign.CENTER);
    private final STextField<Integer> secondTextField = STextField.of(new TimeFormat(59))
            .transparentAsLabel()
            .hAlign(HAlign.CENTER);

    // ===========================================================================================================
    // CONSTRUCTOR

    public SLocalTimePicker() {
        this(null);
    }

    public SLocalTimePicker(LocalTime localTime) {

        // setup defaults
        value(localTime);
        hourTextField.onFocusLost(e -> this.manualTyped());
        minuteTextField.onFocusLost(e -> this.manualTyped());
        secondTextField.onFocusLost(e -> this.manualTyped());

        // layout
        SMigPanel smigPanel = new SMigPanel().noGaps().noMargins();
        smigPanel.addComponent(iconButton(TIMEPICKER_NEXTHOUR, this::nextHour)).alignX(AlignX.CENTER);
        smigPanel.addComponent(iconButton(TIMEPICKER_NEXTMINUTE, this::nextMinute)).alignX(AlignX.CENTER).skip();
        smigPanel.addComponent(iconButton(TIMEPICKER_NEXTSECOND, this::nextSecond)).alignX(AlignX.CENTER).skip();
        smigPanel.wrap();
        smigPanel.addComponent(hourTextField).sizeGroup("time").alignX(AlignX.CENTER).growX();
        smigPanel.addComponent(new JLabel(":")).sizeGroup("sep").alignX(AlignX.CENTER);
        smigPanel.addComponent(minuteTextField).sizeGroup("time").alignX(AlignX.CENTER).growX();
        smigPanel.addComponent(new JLabel(":")).sizeGroup("sep").alignX(AlignX.CENTER);
        smigPanel.addComponent(secondTextField).sizeGroup("time").alignX(AlignX.CENTER).growX();
        smigPanel.wrap();
        smigPanel.addComponent(iconButton(TIMEPICKER_PREVHOUR, this::prevHour)).alignX(AlignX.CENTER);
        smigPanel.addComponent(iconButton(TIMEPICKER_PREVMINUTE, this::prevMinute)).alignX(AlignX.CENTER).skip();
        smigPanel.addComponent(iconButton(TIMEPICKER_PREVSECOND, this::prevSecond)).alignX(AlignX.CENTER).skip();

        setLayout(new BorderLayout());
        add(smigPanel, BorderLayout.CENTER);

        updateComponents();
    }

    private SButton iconButton(SIconRegistry.SwayInternallyUsedIcon icon, Runnable runnable) {
        return SButton.of(SIconRegistry.find(icon))
                .asImageButton()
                .onAction(e -> runnable.run());
    }

    private LocalTime unnull(LocalTime value) {
        return value == null ? LocalTime.now() : value;
    }
    private int decrease(int value, int max) {
        return (value <= 0 ? max : value - 1);
    }
    private int increase(int value, int max) {
        return (value >= max ? 0 : value + 1);
    }

    private void manualTyped() {
        // If there is a value, but one part becomes null, all becomes null
        if (value != null && (hourTextField.getValue() == null || minuteTextField.getValue() == null || secondTextField.getValue() == null)) {
            value = null;
        }
        else {
            value = unnull(value);
            value = value.withHour(hourTextField.getValue() != null ? hourTextField.getValue() : value.getHour())
                    .withMinute(minuteTextField.getValue() != null ? minuteTextField.getValue() : value.getMinute())
                    .withSecond(secondTextField.getValue() != null ? secondTextField.getValue() : value.getSecond());
        }
        value(value);
        updateComponents();
    }

    private void prevHour() {
        value = unnull(value);
        value(value.withHour(decrease(value.getHour(), 23)));
    }

    private void nextHour() {
        value = unnull(value);
        value(value.withHour(increase(value.getHour(), 23)));
    }

    private void prevMinute() {
        value = unnull(value);
        value(value.withMinute(decrease(value.getMinute(), 59)));
    }

    private void nextMinute() {
        value = unnull(value);
        value(value.withMinute(increase(value.getMinute(), 59)));
    }

    private void prevSecond() {
        value = unnull(value);
        value(value.withSecond(decrease(value.getSecond(), 59)));
    }

    private void nextSecond() {
        value = unnull(value);
        value(value.withSecond(increase(value.getSecond(), 59)));
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


    // ===========================================================================================================
    // LAYOUT

    protected void updateComponents() {
        hourTextField.setValue(value == null ? null : value.getHour());
        minuteTextField.setValue(value == null ? null : value.getMinute());
        secondTextField.setValue(value == null ? null : value.getSecond());
    }


    // =============================================================================
    // SUPPORT


    static public SLocalTimePicker of() {
        return new SLocalTimePicker();
    }
}

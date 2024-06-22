package org.tbee.sway;

import net.miginfocom.layout.AlignX;
import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;
import org.tbee.sway.binding.ExceptionHandler;
import org.tbee.sway.mixin.ExceptionHandlerMixin;
import org.tbee.sway.mixin.JComponentMixin;
import org.tbee.sway.mixin.ValueMixin;

import javax.swing.JPanel;
import java.beans.PropertyVetoException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;
import java.util.Objects;

public class SLocalDateTimePicker extends JPanel implements
        ValueMixin<SLocalDateTimePicker, LocalDateTime>,
        ExceptionHandlerMixin<SLocalDateTimePicker>,
        JComponentMixin<SLocalDateTimePicker> {

    private final SLocalDatePicker datePicker = SLocalDatePicker.of().mode(SLocalDatePicker.Mode.SINGLE);
    private final SLocalTimePicker timePicker = SLocalTimePicker.of();

    // ===========================================================================================================
    // CONSTRUCTOR

    public SLocalDateTimePicker() {
        this(null);
    }

    public SLocalDateTimePicker(LocalDateTime localDateTime) {

        // setup defaults
        value(localDateTime);

        // layout
        setLayout(new MigLayout());
        add(datePicker, new CC().alignX(AlignX.CENTER).wrap());
        add(timePicker, new CC().alignX(AlignX.CENTER));

        // Adopt changes
        datePicker.value$().onChange(v -> deriveValue());
        timePicker.value$().onChange(v -> deriveValue());
    }

    private void deriveValue() {
        if (derivingValue > 0) {
            return;
        }
        derivingValue++;

        try {
            LocalDate localDate = datePicker.getValue();
            LocalTime localTime = timePicker.getValue();

            // If either changed to null -> null
            if (value != null && (localDate == null || localTime == null)) {
                setValue(null);
                return;
            }

            // Make sure neither is null
            localDate = (localDate == null ? LocalDate.now() : localDate);
            localTime = (localTime == null ? LocalTime.now() : localTime);
            LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
            setValue(localDateTime);
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

    public LocalDateTime getValue() {
        return value;
    }
    public void setValue(LocalDateTime v) {
        try {
            boolean changed = !Objects.equals(this.value, v);

            fireVetoableChange(VALUE, this.value, v);
            firePropertyChange(VALUE, this.value, this.value = v);

            if (changed) {
                datePicker.setValue(v == null ? null : v.toLocalDate());
                timePicker.setValue(v == null ? null : v.toLocalTime());
            }
        }
        catch (PropertyVetoException e) {
            throw new IllegalArgumentException(e);
        }
    }
    private LocalDateTime value = null;


    /**
     * Locale: determines the language of the labels
     */
    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale v) {
        try {
            fireVetoableChange(LOCALE, this.locale, v);
            firePropertyChange(LOCALE, this.locale, this.locale = v);
            datePicker.locale(v);
        }
        catch (PropertyVetoException e) {
            throw new IllegalArgumentException(e);
        }
    }
    private Locale locale = datePicker.getLocale();
    final static public String LOCALE = "locale";
    public SLocalDateTimePicker locale(Locale v) {
        setLocale(v);
        return this;
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
    public SLocalDateTimePicker showSeconds(boolean v) {
        setShowSeconds(v);
        return this;
    }

    // ===========================================================================================================
    // LAYOUT


    // =============================================================================
    // FLUNT API

    static public SLocalDateTimePicker of() {
        return new SLocalDateTimePicker();
    }
}

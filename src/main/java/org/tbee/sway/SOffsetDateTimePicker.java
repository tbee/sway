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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Locale;
import java.util.Objects;

public class SOffsetDateTimePicker extends JPanel implements
        ValueMixin<SOffsetDateTimePicker, OffsetDateTime>,
        ExceptionHandlerMixin<SOffsetDateTimePicker>,
        JComponentMixin<SOffsetDateTimePicker> {

    private final SLocalDatePicker datePicker = SLocalDatePicker.of().mode(SLocalDatePicker.Mode.SINGLE);
    private final SLocalTimePicker timePicker = SLocalTimePicker.of().showClear(false);
    private final SZoneOffsetPicker zonePicker = SZoneOffsetPicker.of();

    // ===========================================================================================================
    // CONSTRUCTOR

    public SOffsetDateTimePicker() {
        this(null);
    }

    public SOffsetDateTimePicker(OffsetDateTime offsetDateTime) {

        // setup defaults
        setValue(offsetDateTime);

        // layout
        setLayout(new MigLayout(new LC().gridGap("0", "0").insets("0")));
        add(datePicker, new CC().alignX(AlignX.CENTER).wrap());
        add(SHPanel.of(timePicker, zonePicker), new CC().alignX(AlignX.CENTER));

        // Adopt changes
        datePicker.value$().onChange(v -> deriveValue());
        timePicker.value$().onChange(v -> deriveValue());
        zonePicker.value$().onChange(v -> deriveValue());
    }

    private void deriveValue() {
        if (derivingValue > 0) {
            return;
        }
        derivingValue++;

        try {
            LocalDate localDate = datePicker.getValue();
            LocalTime localTime = timePicker.getValue();
            ZoneOffset offset = zonePicker.getValue();

            // If either changed to null -> null
            if (value != null && (localDate == null || localTime == null || offset == null)) {
                setValue(null);
                return;
            }

            // Make sure none is null
            localDate = (localDate == null ? LocalDate.now() : localDate);
            localTime = (localTime == null ? LocalTime.now() : localTime);
            offset = (offset == null ? ZoneOffset.UTC : offset); // TODO: how to get system default offset?
            OffsetDateTime offsetDateTime = OffsetDateTime.of(LocalDateTime.of(localDate, localTime), offset);
            setValue(offsetDateTime);
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

    public OffsetDateTime getValue() {
        return value;
    }
    public void setValue(OffsetDateTime v) {
        try {
            boolean changed = !Objects.equals(this.value, v);

            fireVetoableChange(VALUE, this.value, v);
            firePropertyChange(VALUE, this.value, this.value = v);

            if (changed) {
                datePicker.setValue(v == null ? null : v.toLocalDate());
                timePicker.setValue(v == null ? null : v.toLocalTime());
                zonePicker.setValue(v == null ? null : v.getOffset());
            }
        }
        catch (PropertyVetoException e) {
            throw new IllegalArgumentException(e);
        }
    }
    private OffsetDateTime value = null;


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
    public SOffsetDateTimePicker locale(Locale v) {
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
    public SOffsetDateTimePicker showSeconds(boolean v) {
        setShowSeconds(v);
        return this;
    }

    // ===========================================================================================================
    // LAYOUT


    // =============================================================================
    // FLUNT API

    static public SOffsetDateTimePicker of() {
        return new SOffsetDateTimePicker();
    }
}

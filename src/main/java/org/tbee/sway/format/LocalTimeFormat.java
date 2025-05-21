package org.tbee.sway.format;

import net.miginfocom.layout.AlignX;
import org.tbee.sway.SConfirmDialog;
import org.tbee.sway.SDialog;
import org.tbee.sway.SLocalTimePicker;
import org.tbee.sway.SMigPanel;
import org.tbee.sway.support.HAlign;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.Locale;

/**
 * This format always allows typing in iso notation.
 */
public class LocalTimeFormat implements Format<LocalTime> {

    private final DateTimeFormatter dateTimeFormatter;

    public LocalTimeFormat(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    public LocalTimeFormat() {
        this(FormatStyle.MEDIUM);
    }

    public LocalTimeFormat(Locale locale) {
        this(FormatStyle.MEDIUM, locale);
    }

    public LocalTimeFormat(FormatStyle formatStyle, Locale locale) {
        this(DateTimeFormatter.ofLocalizedTime(formatStyle).withLocale(locale));
    }

    public LocalTimeFormat(FormatStyle formatStyle) {
        this(DateTimeFormatter.ofLocalizedTime(formatStyle));
    }

    @Override
    public String toString(LocalTime value) {
        return value == null ? "" : dateTimeFormatter.format(value);
    }

    @Override
    public LocalTime toValue(String string) {
        try {
            return string.isBlank() ? null : LocalTime.from(dateTimeFormatter.parse(string));
        }
        catch (DateTimeParseException e) {

            // try the fallback ISO notation
            try {
                return LocalTime.from(DateTimeFormatter.ISO_LOCAL_TIME.parse(string));
            }
            catch (DateTimeParseException e2) {

                // If that fails, throw the original exception with a typing hint
                throw new DateTimeParseException(e.getMessage() + ", example: " + dateTimeFormatter.format(LocalDate.now()), e.getParsedString(), e.getErrorIndex(), e);
            }
        }
    }

    @Override
    public int columns() {
        return LocalTime.of(22, 22, 22).format(dateTimeFormatter).length();
    }

    @Override
    public HAlign horizontalAlignment() {
        return HAlign.LEADING;
    }


    // ==============================================
    // EDITOR

    @Override
    public Editor<LocalTime> editor() {
        return (owner, value, callback) -> {
            SLocalTimePicker timePicker =  new SLocalTimePicker().value(value);
            SMigPanel migPanel = SMigPanel.of().fill();
            migPanel.addComponent(timePicker).alignX(AlignX.CENTER); // otherwise the time picker is aligned left in the dialog
            SConfirmDialog.of(owner, "", migPanel)
                    .onCancelJustClose()
                    .onOk(() -> callback.accept(timePicker.getValue()))
                    .showAndWait();
        };
    }
}
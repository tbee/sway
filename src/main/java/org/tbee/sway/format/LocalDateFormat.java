package org.tbee.sway.format;

import org.tbee.sway.SConfirmDialog;
import org.tbee.sway.SDialog;
import org.tbee.sway.SLocalDatePicker;
import org.tbee.sway.support.HAlign;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.Locale;

/**
 * This format always allows typing in iso notation.
 */
public class LocalDateFormat implements Format<LocalDate> {

    private final DateTimeFormatter dateTimeFormatter;
    static private LocalDate longestValue = LocalDate.of(8888,12, 23);

    public LocalDateFormat(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    public LocalDateFormat() {
        this(FormatStyle.MEDIUM);
    }

    public LocalDateFormat(Locale locale) {
        this(FormatStyle.MEDIUM, locale);
    }

    public LocalDateFormat(FormatStyle formatStyle, Locale locale) {
        this(DateTimeFormatter.ofLocalizedDate(formatStyle).withLocale(locale));
    }

    public LocalDateFormat(FormatStyle formatStyle) {
        this(DateTimeFormatter.ofLocalizedDate(formatStyle));
    }

    @Override
    public String toString(LocalDate value) {
        return value == null ? "" : dateTimeFormatter.format(value);
    }

    @Override
    public LocalDate toValue(String string) {
        try {
            return string.isBlank() ? null : LocalDate.from(dateTimeFormatter.parse(string));
        }
        catch (DateTimeParseException e) {

            // try the fallback ISO notation
            try {
                return LocalDate.from(DateTimeFormatter.ISO_LOCAL_DATE.parse(string));
            }
            catch (DateTimeParseException e2) {

                // If that fails, throw the original exception with a typing hint
                throw new DateTimeParseException(e.getMessage() + ", example: " + dateTimeFormatter.format(LocalDate.now()), e.getParsedString(), e.getErrorIndex(), e);
            }
        }
    }

    @Override
    public int columns() {
        return dateTimeFormatter.format(longestValue).length();
    }

    @Override
    public HAlign horizontalAlignment() {
        return HAlign.LEADING;
    }


    // ==============================================
    // EDITOR

    @Override
    public Editor<LocalDate> editor() {
        return (owner, value, callback) -> {
            SLocalDatePicker datePicker =  new SLocalDatePicker().margin(0, 5, 10, 5)
                    .value(value)
                    .displayedLocalDate(value != null ? value : LocalDate.now());

            SConfirmDialog sDialog = SConfirmDialog.of(owner, "", datePicker);

            datePicker.value$().onChange((LocalDate ld) -> {
                callback.accept(ld);
                sDialog.close();
            });

            sDialog.showAndWait();
        };
    }
}
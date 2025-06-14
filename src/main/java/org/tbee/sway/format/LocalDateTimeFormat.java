package org.tbee.sway.format;

import org.tbee.sway.SConfirmDialog;
import org.tbee.sway.SDialog;
import org.tbee.sway.SLocalDateTimePicker;
import org.tbee.sway.support.HAlign;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.Locale;

/**
 * This format always allows typing in iso notation.
 */
public class LocalDateTimeFormat implements Format<LocalDateTime> {

    private final DateTimeFormatter dateTimeFormatter;
    static private LocalDateTime longestValue = LocalDateTime.of(8888,12, 23, 12, 58, 59);

    public LocalDateTimeFormat(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    public LocalDateTimeFormat() {
        this(FormatStyle.MEDIUM);
    }

    public LocalDateTimeFormat(Locale locale) {
        this(FormatStyle.MEDIUM, locale);
    }

    public LocalDateTimeFormat(FormatStyle formatStyle, Locale locale) {
        this(DateTimeFormatter.ofLocalizedDateTime(formatStyle).withLocale(locale));
    }

    public LocalDateTimeFormat(FormatStyle formatStyle) {
        this(DateTimeFormatter.ofLocalizedDateTime(formatStyle));
    }

    @Override
    public String toString(LocalDateTime value) {
        return value == null ? "" : dateTimeFormatter.format(value);
    }

    @Override
    public LocalDateTime toValue(String string) {
        try {
            return string.isBlank() ? null : LocalDateTime.from(dateTimeFormatter.parse(string));
        }
        catch (DateTimeParseException e) {

            // try the fallback ISO notation
            try {
                return LocalDateTime.from(DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(string));
            }
            catch (DateTimeParseException e2) {

                // If that fails, throw the original exception with a typing hint
                throw new DateTimeParseException(e.getMessage() + ", example: " + dateTimeFormatter.format(LocalDateTime.now()), e.getParsedString(), e.getErrorIndex(), e);
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
    public Editor<LocalDateTime> editor() {
        return (owner, value, callback) -> {
            SLocalDateTimePicker dateTimePicker =  new SLocalDateTimePicker().margin(0, 5, 10, 5).value(value);
            SConfirmDialog.of(owner, "", dateTimePicker)
                    .onCancelJustClose()
                    .onOk(() -> callback.accept(dateTimePicker.getValue()))
                    .showAndWait();
        };
    }
}
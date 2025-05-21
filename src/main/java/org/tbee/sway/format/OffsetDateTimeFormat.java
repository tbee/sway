package org.tbee.sway.format;

import org.tbee.sway.SConfirmDialog;
import org.tbee.sway.SDialog;
import org.tbee.sway.SOffsetDateTimePicker;
import org.tbee.sway.support.HAlign;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * This format always allows typing in iso notation.
 */
public class OffsetDateTimeFormat implements Format<OffsetDateTime> {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    static private OffsetDateTime longestValue = OffsetDateTime.of(LocalDateTime.of(8888,12, 23, 12, 58, 59), ZoneOffset.ofHours(10));

    @Override
    public String toString(OffsetDateTime value) {
        return value == null ? "" : dateTimeFormatter.format(value);
    }

    @Override
    public OffsetDateTime toValue(String string) {
        try {
            return string.isBlank() ? null : OffsetDateTime.from(dateTimeFormatter.parse(string));
        }
        catch (DateTimeParseException e) {
            // If that fails, throw the original exception with a typing hint
            throw new DateTimeParseException(e.getMessage() + ", example: " + dateTimeFormatter.format(OffsetDateTime.now()), e.getParsedString(), e.getErrorIndex(), e);
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
    public Editor<OffsetDateTime> editor() {
        return (owner, value, callback) -> {
            SOffsetDateTimePicker offsetDateTimePicker =  new SOffsetDateTimePicker().margin(0, 5, 10, 5).value(value);
            SConfirmDialog.of(owner, "", offsetDateTimePicker)
                    .onCancelJustClose()
                    .onOk(() -> callback.accept(offsetDateTimePicker.getValue()))
                    .showAndWait();
        };
    }
}
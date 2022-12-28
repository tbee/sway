package org.tbee.sway.format;

import org.tbee.sway.support.HorizontalAlignment;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * This format always allows typing in iso notation.
 */
public class ZonedDateTimeFormat implements Format<ZonedDateTime> {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;
    static private ZonedDateTime longestValue = ZonedDateTime.of(LocalDateTime.of(8888,12, 23, 12, 58, 59), ZoneId.of("Europe/Amsterdam"));

    @Override
    public String toString(ZonedDateTime value) {
        return value == null ? "" : dateTimeFormatter.format(value);
    }

    @Override
    public ZonedDateTime toValue(String string) {
        try {
            return string.isBlank() ? null : ZonedDateTime.from(dateTimeFormatter.parse(string));
        }
        catch (DateTimeParseException e) {
            // If that fails, throw the original exception with a typing hint
            throw new DateTimeParseException(e.getMessage() + ", example: " + dateTimeFormatter.format(ZonedDateTime.now()), e.getParsedString(), e.getErrorIndex(), e);
        }
    }

    @Override
    public int columns() {
        return dateTimeFormatter.format(longestValue).length();
    }

    @Override
    public HorizontalAlignment horizontalAlignment() {
        return HorizontalAlignment.LEADING;
    }
}
package org.tbee.sway.format;

import org.tbee.sway.support.HAlign;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * This format always allows typing in iso notation.
 */
public class DateFormat implements Format<Date> {

    LocalDateTimeFormat localDateTimeFormat = new LocalDateTimeFormat();


    @Override
    public String toString(Date value) {
        LocalDateTime localDateTime = null;
        if (value != null) {
            localDateTime = new java.sql.Timestamp(value.getTime()).toLocalDateTime();
        }
        return value == null ? "" : localDateTimeFormat.toString(localDateTime);
    }

    @Override
    public Date toValue(String string) {
        LocalDateTime localDateTime = string.isBlank() ? null : localDateTimeFormat.toValue(string);
        // The documentation explicitly warns to “not view Timestamp values generically as an instance of java.util.Date”.
        // This most notably fails in the equals method, where it will never be equals when compared to a Date.
        // So after using it to convert, create a real java.util.date
        Date date = null;
        if (localDateTime != null ) {
            Timestamp timestamp = Timestamp.valueOf(localDateTime);
            long milliseconds = timestamp.getTime() + (timestamp.getNanos() / 1000000);
            date = new java.util.Date(milliseconds);
        }
        return date;
    }

    @Override
    public int columns() {
        return localDateTimeFormat.columns();
    }

    @Override
    public HAlign horizontalAlignment() {
        return localDateTimeFormat.horizontalAlignment();
    }
}
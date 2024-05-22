package org.tbee.sway.format;

import org.tbee.sway.support.HAlign;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * This format always allows typing in iso notation.
 */
public class CalendarFormat implements Format<Calendar> {

    ZonedDateTimeFormat zonedDateTimeFormat = new ZonedDateTimeFormat();


    @Override
    public String toString(Calendar value) {
        ZonedDateTime zonedDateTime = null;
        if (value != null) {
            Instant instant = value.toInstant();
            ZoneId zoneId = TimeZone.getDefault().toZoneId();
            zonedDateTime = ZonedDateTime.ofInstant(instant, zoneId);
        }
        return value == null ? "" : zonedDateTimeFormat.toString(zonedDateTime);
    }

    @Override
    public Calendar toValue(String string) {
        ZonedDateTime zonedDateTime = string.isBlank() ? null : zonedDateTimeFormat.toValue(string);
        return zonedDateTime == null ? null : GregorianCalendar.from(zonedDateTime);
    }

    @Override
    public int columns() {
        return zonedDateTimeFormat.columns();
    }

    @Override
    public HAlign horizontalAlignment() {
        return zonedDateTimeFormat.horizontalAlignment();
    }
}
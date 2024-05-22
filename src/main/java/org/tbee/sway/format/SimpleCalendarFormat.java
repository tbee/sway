package org.tbee.sway.format;

import org.tbee.sway.support.HAlign;

import java.text.ParseException;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;

public class SimpleCalendarFormat implements Format<Calendar> {
    static private Date longestValue = new Date(1,11, 23);

    private final java.text.SimpleDateFormat simpleDateFormat;

    public SimpleCalendarFormat(String pattern) {
        this(new java.text.SimpleDateFormat(pattern));
    }

    public SimpleCalendarFormat(java.text.SimpleDateFormat simpleDateFormat) {
        this.simpleDateFormat = simpleDateFormat;
    }

    @Override
    public String toString(Calendar value) {
        return value == null ? "" : simpleDateFormat.format(value.getTime());
    }

    @Override
    public Calendar toValue(String string) {
        try {
            Date date = string.isBlank() ? null : simpleDateFormat.parse(string);
            Calendar calendar = null;
            if (date != null) {
                calendar = Calendar.getInstance();
                calendar.setTime(date);
            }
            return calendar;
        }
        catch (ParseException e) {
            throw new DateTimeParseException(e.getMessage() + ", example: " + simpleDateFormat.format(Calendar.getInstance()), string, 0, e);
        }
    }

    @Override
    public int columns() {
        return simpleDateFormat.format(longestValue).length();
    }

    @Override
    public HAlign horizontalAlignment() {
        return HAlign.LEADING;
    }
}
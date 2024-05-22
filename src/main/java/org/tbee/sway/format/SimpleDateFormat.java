package org.tbee.sway.format;

import org.tbee.sway.support.HAlign;

import java.text.ParseException;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class SimpleDateFormat implements Format<Date> {
    static private Date longestValue = new Date(1,11, 23);

    private final java.text.SimpleDateFormat simpleDateFormat;

    public SimpleDateFormat(String pattern) {
        this(new java.text.SimpleDateFormat(pattern));
    }

    public SimpleDateFormat(java.text.SimpleDateFormat simpleDateFormat) {
        this.simpleDateFormat = simpleDateFormat;
    }

    @Override
    public String toString(Date value) {
        return value == null ? "" : simpleDateFormat.format(value);
    }

    @Override
    public Date toValue(String string) {
        try {
            return string.isBlank() ? null : simpleDateFormat.parse(string);
        }
        catch (ParseException e) {
            throw new DateTimeParseException(e.getMessage() + ", example: " + simpleDateFormat.format(new Date()), string, 0, e);
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
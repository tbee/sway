package org.tbee.sway;

import org.tbee.sway.format.BigDecimalFormat;
import org.tbee.sway.format.BigIntegerFormat;
import org.tbee.sway.format.CalendarFormat;
import org.tbee.sway.format.DateFormat;
import org.tbee.sway.format.DoubleFormat;
import org.tbee.sway.format.FileFormat;
import org.tbee.sway.format.Format;
import org.tbee.sway.format.IntegerFormat;
import org.tbee.sway.format.LocalDateFormat;
import org.tbee.sway.format.LocalDateTimeFormat;
import org.tbee.sway.format.LocalTimeFormat;
import org.tbee.sway.format.LongFormat;
import org.tbee.sway.format.OffsetDateTimeFormat;
import org.tbee.sway.format.StringFormat;
import org.tbee.sway.format.URIFormat;
import org.tbee.sway.format.URLFormat;
import org.tbee.sway.format.ZonedDateTimeFormat;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SFormatRegistry {

    private static Map<Class<?>, Format<?>> formats = new HashMap<>();

    /**
     * Register additional formats. These will override predefined ones.
     * This can be used to register once formats for e.g. ValueTypes.
     * @param clazz
     * @param format
     */
    static public void register(Class<?> clazz, Format<?> format) {
        formats.put(clazz, format);
    }
    static public boolean unregister(Class<?> clazz) {
        return formats.remove(clazz) != null;
    }

    /**
     * Get the format for a specific class.
     * @param clazz
     * @return
     */
    static public Format<?> findFor(Class<?> clazz) {
        Format<?> format = formats.get(clazz);
        if (format != null) {
            return format;
        }

        if (clazz.equals(String.class)) return new StringFormat();
        if (clazz.equals(Integer.class)) return new IntegerFormat();
        if (clazz.equals(Long.class)) return new LongFormat();
        if (clazz.equals(Double.class)) return new DoubleFormat();
        if (clazz.equals(BigInteger.class)) return new BigIntegerFormat();
        if (clazz.equals(BigDecimal.class)) return new BigDecimalFormat();
        if (clazz.equals(LocalTime.class)) return new LocalTimeFormat();
        if (clazz.equals(LocalDate.class)) return new LocalDateFormat();
        if (clazz.equals(LocalDateTime.class)) return new LocalDateTimeFormat();
        if (clazz.equals(OffsetDateTime.class)) return new OffsetDateTimeFormat();
        if (clazz.equals(ZonedDateTime.class)) return new ZonedDateTimeFormat();
        if (clazz.equals(Calendar.class)) return new CalendarFormat();
        if (clazz.equals(Date.class)) return new DateFormat();
        if (clazz.equals(URL.class)) return new URLFormat();
        if (clazz.equals(URI.class)) return new URIFormat();
        if (clazz.equals(File.class)) return new FileFormat();
//        if (clazz.equals(Integer.class)) return new JavaFormat<Integer>(NumberFormat.getIntegerInstance(), ("" + Integer.MIN_VALUE).length(), SwingConstants.TRAILING);
        return null;
    }
}

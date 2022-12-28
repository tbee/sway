package org.tbee.sway.format;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

public class FormatRegistry {

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

        if (clazz.equals(String.class)) return new StringFormat(false);
        if (clazz.equals(Integer.class)) return new IntegerFormat();
        if (clazz.equals(Long.class)) return new LongFormat();
        if (clazz.equals(Double.class)) return new DoubleFormat();
        if (clazz.equals(BigInteger.class)) return new BigIntegerFormat();
        if (clazz.equals(BigDecimal.class)) return new BigDecimalFormat();
        if (clazz.equals(LocalDate.class)) return new LocalDateFormat();
        if (clazz.equals(LocalDateTime.class)) return new LocalDateTimeFormat();
        if (clazz.equals(OffsetDateTime.class)) return new OffsetDateTimeFormat();
        if (clazz.equals(ZonedDateTime.class)) return new ZonedDateTimeFormat();
//        if (clazz.equals(Integer.class)) return new JavaFormat<Integer>(NumberFormat.getIntegerInstance(), ("" + Integer.MIN_VALUE).length(), SwingConstants.TRAILING);
        return null;
    }
}

package org.tbee.sway.format;

import javax.swing.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
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
    static public boolean unregister(Format<?> format) {
        return formats.remove(format) != null;
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
        if (clazz.equals(Integer.class)) return new JavaFormat<Integer>(NumberFormat.getIntegerInstance(), ("" + Integer.MIN_VALUE).length(), SwingConstants.TRAILING);
        if (clazz.equals(BigInteger.class)) return new BigIntegerFormat();
        if (clazz.equals(BigDecimal.class)) return new BigDecimalFormat();
        throw new IllegalStateException("No format found for " + clazz);
    }
}

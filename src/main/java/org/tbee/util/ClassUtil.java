package org.tbee.util;

import java.util.HashMap;
import java.util.Map;

public class ClassUtil {

    private final static Map<Class<?>, Class<?>> primitiveToClassMap = new HashMap<>();
    static {
        primitiveToClassMap.put(boolean.class, Boolean.class);
        primitiveToClassMap.put(byte.class, Byte.class);
        primitiveToClassMap.put(short.class, Short.class);
        primitiveToClassMap.put(char.class, Character.class);
        primitiveToClassMap.put(int.class, Integer.class);
        primitiveToClassMap.put(long.class, Long.class);
        primitiveToClassMap.put(float.class, Float.class);
        primitiveToClassMap.put(double.class, Double.class);
    }

    static public Class<?> primitiveToClass(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            return primitiveToClassMap.get(clazz);
        }
        return clazz;
    }
}

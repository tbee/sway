package org.tbee.sway.format;

/**
 * If you are sure only toValue needs to be implemented, this enable the use of a lambda.
 * @param <T>
 */
public interface FormatToValue<T> extends Format<T> {
    default String toString(T value) {
        throw new RuntimeException("Not implemented");
    };

    T toValue(String string);
}
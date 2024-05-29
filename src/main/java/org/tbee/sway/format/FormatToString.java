package org.tbee.sway.format;

/**
 * If you are sure only toString needs to be implemented, this enable the use of a lambda.
 * @param <T>
 */
public interface FormatToString<T> extends Format<T> {
    String toString(T value);

    default T toValue(String string) {
        throw new RuntimeException("Not implemented");
    }
}
package org.tbee.sway.format;

public class StringFormat implements Format<String> {

    @Override
    public String toString(String value) {
        return value;
    }

    @Override
    public String toValue(String string) {
        return string;
    }

    @Override
    public int columns() {
        return 25;
    }
}
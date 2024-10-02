package org.tbee.sway.format;

import org.tbee.sway.support.HAlign;

public class StringFormat implements Format<String> {

    private final String stringForNull;

    public StringFormat() {
        // If you don't want to see this symbol, then simply make sure no null is being rendered, or set your own.
        this("∅");
    }

    public StringFormat(String stringForNull) {
        this.stringForNull = stringForNull;
    }

    @Override
    public String toString(String value) {
        return value == null ? stringForNull : value;
    }

    @Override
    public String toValue(String string) {
        return stringForNull.equals(string) ? null : string;
    }

    @Override
    public int columns() {
        return 25;
    }

    @Override
    public HAlign horizontalAlignment() {
        return HAlign.LEADING;
    }
}
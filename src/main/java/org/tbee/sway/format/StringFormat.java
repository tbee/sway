package org.tbee.sway.format;

import org.tbee.sway.SIconRegistry;
import org.tbee.sway.support.HAlign;

import javax.swing.Icon;

public class StringFormat implements Format<String> {

    private final Icon iconForNull;

    public StringFormat() {
        // If you don't want to see this icon, then simply make sure no null is being rendered, or set your own.
        this(SIconRegistry.find(SIconRegistry.SwayInternallyUsedIcon.FORMAT_NULL));
    }

    public StringFormat(Icon iconForNull) {
        this.iconForNull = iconForNull;
    }

    @Override
    public String toString(String value) {
        return value;
    }

    @Override
    public Icon toIcon(String value) {
        return value == null ? iconForNull : null;
    }

    @Override
    public String toValue(String string) {
        return string;
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
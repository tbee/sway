package org.tbee.sway.format;

import java.text.FieldPosition;
import java.text.ParsePosition;

public class FormatAsJavaTextFormat extends java.text.Format {

    final private Format format;
    public FormatAsJavaTextFormat(Format format) {
        this.format = format;
    }

    @Override
    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        return new StringBuffer(format.toString(obj));
    }

    @Override
    public Object parseObject(String source, ParsePosition pos) {
        return format.toValue(source);
    }
}
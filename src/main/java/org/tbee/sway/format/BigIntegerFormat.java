package org.tbee.sway.format;

import org.tbee.sway.support.HorizontalAlignment;

import java.math.BigInteger;

public class BigIntegerFormat implements Format<BigInteger> {

    @Override
    public String toString(BigInteger value) {
        return value == null ? "" : value.toString();
    }

    @Override
    public BigInteger toValue(String string) {
        return string.isBlank() ? null : new BigInteger(string);
    }

    @Override
    public int columns() {
        return 15;
    }

    @Override
    public HorizontalAlignment horizontalAlignment() {
        return HorizontalAlignment.TRAILING;
    }
}
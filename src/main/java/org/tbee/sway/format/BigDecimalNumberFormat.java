package org.tbee.sway.format;

import org.tbee.sway.support.HAlign;

import java.math.BigDecimal;
import java.text.NumberFormat;

public class BigDecimalNumberFormat implements Format<BigDecimal> {

    private final NumberFormat numberFormat;

    public BigDecimalNumberFormat() {
        this(NumberFormat.getInstance());
    }

    public BigDecimalNumberFormat(NumberFormat numberFormat) {
        this.numberFormat = numberFormat;
    }

    public BigDecimalNumberFormat minimumFractionDigits(int v) {
        numberFormat.setMinimumFractionDigits(v);
        return this;
    }

    public BigDecimalNumberFormat maximumFractionDigits(int v) {
        numberFormat.setMinimumFractionDigits(v);
        return this;
    }

    @Override
    public String toString(BigDecimal value) {
        return value == null ? "" : numberFormat.format(value);
    }

    @Override
    public BigDecimal toValue(String string) {
        BigDecimal bigDecimal = string.isBlank() ? null : new BigDecimal(string);
        if (bigDecimal.scale() < numberFormat.getMinimumFractionDigits()) {
            bigDecimal = bigDecimal.setScale(numberFormat.getMinimumFractionDigits());
        }
        if (bigDecimal.scale() > numberFormat.getMaximumFractionDigits()) {
            bigDecimal = bigDecimal.setScale(numberFormat.getMaximumFractionDigits());
        }
        return bigDecimal;
    }

    @Override
    public int columns() {
        return 15;
    }

    @Override
    public HAlign horizontalAlignment() {
        return HAlign.TRAILING;
    }

    public static BigDecimalNumberFormat of() {
        return new BigDecimalNumberFormat();
    }
}

package org.tbee.sway.format;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tbee.sway.support.HAlign;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

public class BigDecimalNumberFormat implements Format<BigDecimal> {

    protected final NumberFormat numberFormat;

    public BigDecimalNumberFormat() {
        this(NumberFormat.getInstance());
    }

    BigDecimalNumberFormat(NumberFormat numberFormat) {
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
        return parse(string, this.numberFormat);
    }

    protected @NonNull BigDecimal parse(String string, NumberFormat... numberFormats) {
        for (NumberFormat numberFormat : numberFormats) {
            try {
                ((DecimalFormat) numberFormat).setParseBigDecimal(true);
                BigDecimal bigDecimal = string.isBlank() ? null : (BigDecimal) numberFormat.parse(string);
                int minimumFractionDigits = determineMinimumFractionDigits();
                int maximumFractionDigits = determineMaximumFractionDigits();
                if (bigDecimal.scale() < minimumFractionDigits) {
                    bigDecimal = bigDecimal.setScale(minimumFractionDigits, RoundingMode.HALF_UP);
                }
                if (bigDecimal.scale() > maximumFractionDigits) {
                    bigDecimal = bigDecimal.setScale(maximumFractionDigits, RoundingMode.HALF_UP);
                }
                return bigDecimal;
            }
            catch (ParseException e) {
                // when the last one is attempted, throw the exception
                if (numberFormat == numberFormats[numberFormats.length - 1]) {
                    throw new RuntimeException("Error parsing text", e);
                }
            }
        }
        throw new RuntimeException("No NumberFormats");
    }

    protected int determineMaximumFractionDigits() {
        return numberFormat.getMaximumFractionDigits();
    }

    protected int determineMinimumFractionDigits() {
        return numberFormat.getMinimumFractionDigits();
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
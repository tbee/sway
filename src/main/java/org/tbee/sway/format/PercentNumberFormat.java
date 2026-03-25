package org.tbee.sway.format;

import org.tbee.sway.support.HAlign;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class PercentNumberFormat implements Format<BigDecimal> {

    private final NumberFormat numberFormat;

    public PercentNumberFormat() {
        this(NumberFormat.getPercentInstance());
    }

    public PercentNumberFormat(Locale locale) {
        this(NumberFormat.getPercentInstance(locale));
    }

    public PercentNumberFormat(NumberFormat numberFormat) {
        this.numberFormat = numberFormat;
    }

    public PercentNumberFormat minimumFractionDigits(int v) {
        numberFormat.setMinimumFractionDigits(v);
        return this;
    }

    public PercentNumberFormat maximumFractionDigits(int v) {
        numberFormat.setMinimumFractionDigits(v);
        return this;
    }

    @Override
    public String toString(BigDecimal value) {
        return value == null ? "" : numberFormat.format(value);
    }

    @Override
    public BigDecimal toValue(String string) {
        try {
            ((DecimalFormat)this.numberFormat).setParseBigDecimal(true);
            BigDecimal bigDecimal = string.isBlank() ? null : (BigDecimal) numberFormat.parse(string);
            int minimumFractionDigits = numberFormat.getMinimumFractionDigits() + 2; // Notation is in %, so * 100
            int maximumFractionDigits = numberFormat.getMaximumFractionDigits() + 2; // Notation is in %, so * 100
            if (bigDecimal.scale() < minimumFractionDigits) {
                bigDecimal = bigDecimal.setScale(minimumFractionDigits, RoundingMode.HALF_UP);
            }
            if (bigDecimal.scale() > maximumFractionDigits) {
                bigDecimal = bigDecimal.setScale(maximumFractionDigits, RoundingMode.HALF_UP);
            }
            return bigDecimal;
        }
        catch (ParseException e) {
            throw new RuntimeException("Error parsing text", e);
        }
    }

    @Override
    public int columns() {
        return 15;
    }

    @Override
    public HAlign horizontalAlignment() {
        return HAlign.TRAILING;
    }

    public static PercentNumberFormat of() {
        return new PercentNumberFormat();
    }
}

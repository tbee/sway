package org.tbee.sway.format;

import java.text.NumberFormat;
import java.util.Locale;

public class PercentNumberFormat extends BigDecimalNumberFormat {

    public PercentNumberFormat() {
        this(NumberFormat.getPercentInstance());
    }

    public PercentNumberFormat(Locale locale) {
        this(NumberFormat.getPercentInstance(locale));
    }

    PercentNumberFormat(NumberFormat numberFormat) {
        super(numberFormat);
    }

    @Override
    protected int determineMaximumFractionDigits() {
        return numberFormat.getMaximumFractionDigits() + 2; // Notation is in %, so * 100
    }

    @Override
    protected int determineMinimumFractionDigits() {
        return numberFormat.getMinimumFractionDigits() + 2; // Notation is in %, so * 100
    }

    public static PercentNumberFormat of() {
        return new PercentNumberFormat();
    }
}

package org.tbee.sway.format;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class CurrencyNumberFormat extends BigDecimalNumberFormat {

    protected final NumberFormat lenientNumberFormat;

    CurrencyNumberFormat() {
        this(NumberFormat.getCurrencyInstance(), NumberFormat.getNumberInstance());
    }

    CurrencyNumberFormat(Locale locale) {
        this(NumberFormat.getCurrencyInstance(locale), NumberFormat.getNumberInstance(locale));
    }

    CurrencyNumberFormat(NumberFormat numberFormat, NumberFormat lenientNumberFormat) {
        super(numberFormat);
        this.lenientNumberFormat = lenientNumberFormat;
    }

    public CurrencyNumberFormat currency(Currency v) {
        numberFormat.setCurrency(v);
        return this;
    }

    @Override
    public BigDecimal toValue(String string) {
        return parse(string, this.numberFormat, this.lenientNumberFormat);
    }


    public static CurrencyNumberFormat of() {
        return new CurrencyNumberFormat();
    }

    public static CurrencyNumberFormat of(Locale locale) {
        return new CurrencyNumberFormat(locale);
    }
}

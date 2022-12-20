package org.tbee.sway.format;

import javax.swing.*;
import java.math.BigDecimal;

public class BigDecimalFormat implements Format<BigDecimal> {

    @Override
    public String toString(BigDecimal value) {
        return value == null ? "" : value.toPlainString();
    }

    @Override
    public BigDecimal toValue(String string) {
        return string.isBlank() ? null : new BigDecimal(string);
    }

    @Override
    public int columns() {
        return 15;
    }

    @Override
    public int horizontalAlignment() {
        return SwingConstants.TRAILING;
    }
}
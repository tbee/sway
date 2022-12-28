package org.tbee.sway.support;

import javax.swing.SwingConstants;

public enum HorizontalAlignment {
    LEFT(SwingConstants.LEFT), CENTER(SwingConstants.CENTER), RIGHT(SwingConstants.RIGHT), LEADING(SwingConstants.LEADING), TRAILING(SwingConstants.TRAILING);

    private HorizontalAlignment(int swingConstant) {
        this.swingConstant = swingConstant;

    }

    public int getSwingConstant() {
        return swingConstant;
    }

    final private int swingConstant;
}

package org.tbee.sway.support;

import javax.swing.SwingConstants;

public enum HAlign {
    LEFT(SwingConstants.LEFT), CENTER(SwingConstants.CENTER), RIGHT(SwingConstants.RIGHT), LEADING(SwingConstants.LEADING), TRAILING(SwingConstants.TRAILING);

    private HAlign(int swingConstant) {
        this.swingConstant = swingConstant;

    }

    public int getSwingConstant() {
        return swingConstant;
    }

    final private int swingConstant;

    static public HAlign of(int swingConstant) {
        for (HAlign selectionMode : values()) {
            if (selectionMode.swingConstant == swingConstant) {
                return selectionMode;
            }
        }
        throw new IllegalArgumentException("swingConstant does not exist " + swingConstant);
    }
}

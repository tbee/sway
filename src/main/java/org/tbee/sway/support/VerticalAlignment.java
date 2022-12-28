package org.tbee.sway.support;

import javax.swing.SwingConstants;

public enum VerticalAlignment {
    TOP(SwingConstants.TOP), CENTER(SwingConstants.CENTER), BOTTOM(SwingConstants.BOTTOM);

    private VerticalAlignment(int swingConstant) {
        this.swingConstant = swingConstant;

    }

    public int getSwingConstant() {
        return swingConstant;
    }

    final private int swingConstant;
}

package org.tbee.sway.support;

import javax.swing.SwingConstants;

public enum VAlign {
    TOP(SwingConstants.TOP), CENTER(SwingConstants.CENTER), BOTTOM(SwingConstants.BOTTOM);

    private VAlign(int swingConstant) {
        this.swingConstant = swingConstant;

    }

    public int getSwingConstant() {
        return swingConstant;
    }

    final private int swingConstant;


    static public VAlign of(int swingConstant) {
        for (VAlign selectionMode : values()) {
            if (selectionMode.swingConstant == swingConstant) {
                return selectionMode;
            }
        }
        throw new IllegalArgumentException("swingConstant does not exist " + swingConstant);
    }
}

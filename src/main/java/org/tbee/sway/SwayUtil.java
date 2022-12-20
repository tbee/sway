package org.tbee.sway;

import javax.swing.*;
import java.awt.*;

public class SwayUtil {

    static public Color getErrorColor() {
        try {
            return new Color(UIManager.getColor("Error.color").getRGB());
        }
        catch (NullPointerException e) { // if key is undefined
            return Color.RED; // default value
        }
    }
    static public Color getFirstAlternateRowColor() {
        return new Color(UIManager.getColor("Table.background").getRGB());
    }

    static public Color getSecondAlternateRowColor() {
        return ColorUtil.brighterOrDarker(getFirstAlternateRowColor(), 0.05);
    }
}

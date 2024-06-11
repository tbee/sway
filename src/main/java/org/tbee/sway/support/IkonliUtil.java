package org.tbee.sway.support;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.Icon;
import javax.swing.JLabel;
import java.awt.Color;

public class IkonliUtil {

    static public Icon createIcon(Ikon ikon, int size) {
        return createIcon(ikon, size, new JLabel().getForeground());
    }

    static public Icon createIcon(Ikon ikon, int size, Color color) {
        FontIcon fontIcon = new FontIcon();
        fontIcon.setIkon(ikon);
        fontIcon.setIconSize(size);
        fontIcon.setIconColor(color);
        return fontIcon;
    }
}

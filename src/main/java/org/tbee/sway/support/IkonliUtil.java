package org.tbee.sway.support;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.Icon;

public class IkonliUtil {

    static public Icon createIcon(Ikon ikon, int size) {
        FontIcon fontIcon = new FontIcon();
        fontIcon.setIkon(ikon);
        fontIcon.setIconSize(size);
        return fontIcon;
    }
}

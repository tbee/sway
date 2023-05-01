package org.tbee.sway;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

public class SLookAndFeel {

    public static void installFlatLAFBright() {
        FlatLightLaf.setup();
    }

    public static void installFlatLAFDark() {
        FlatDarkLaf.setup();
    }
}

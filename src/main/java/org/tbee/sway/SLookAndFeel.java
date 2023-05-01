package org.tbee.sway;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

public class SLookAndFeel {

    /**
     * Install the default Look and feel, this might change in the future.
     */
    public static void installDefault() {
        installDefaultBright();
    }

    /**
     * Install the default bright Look and feel, this might change in the future.
     */
    public static void installDefaultBright() {
        installFlatLAFBright();
    }

    /**
     * Install the default dark Look and feel, this might change in the future.
     */
    public static void installDefaultDark() {
        installFlatLAFDark();
    }

    public static void installFlatLAFBright() {
        FlatLightLaf.setup();
    }

    public static void installFlatLAFDark() {
        FlatDarkLaf.setup();
    }
}

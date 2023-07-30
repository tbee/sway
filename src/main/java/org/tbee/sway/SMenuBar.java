package org.tbee.sway;

import javax.swing.*;


/**
 * Just a way to fluently construct menus, so you don't have to deal with all the intermediate variables.
 *
 * <pre>{@code
 *     SMenuBar.of(jFrame)
 *         .add(SMenu.of("menu1")
 *             .add(SMenuItem.of("menuitem 1a", myApp::menuEvent))
 *             .add(SMenuItem.of("menuitem 1b", myApp::menuEvent))
 *         )
 *         .add(SMenu.of("menu2")
 *             .add(SMenuItem.of("menuitem 2a", myApp::menuEvent))
 *             .add(SMenuItem.of("menuitem 2b", myApp::menuEvent))
 *             .add(SMenuItem.of("menuitem 2c", myApp::menuEvent))
 *         )
 *         .add(SMenu.of("menu3")
 *             .add(SMenuItem.of("menuitem 3a", myApp::menuEvent))
 *         );
 * }</pre>
 */
public class SMenuBar extends JMenuBar {

    // ===========================================================================================================================
    // FLUENT API

    static public SMenuBar of() {
        return new SMenuBar();
    }

    static public SMenuBar of(SFrame sFrame) {
        SMenuBar sMenuBar = of();
        sFrame.setJMenuBar(sMenuBar);
        return sMenuBar;
    }

    static public SMenuBar of(SDialog sDialog) {
        SMenuBar sMenuBar = of();
        sDialog.setJMenuBar(sMenuBar);
        return sMenuBar;
    }

    public SMenuBar add(SMenu v) {
        super.add(v);
        return this;
    }

    public SMenuBar name(String v) {
        setName(v);
        return this;
    }

    public SMenuBar enabled(boolean v) {
        setEnabled(v);
        return this;
    }

    public SMenuBar visible(boolean v) {
        setVisible(v);
        return this;
    }
}

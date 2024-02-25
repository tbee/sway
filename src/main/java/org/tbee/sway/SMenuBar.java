package org.tbee.sway;

import org.tbee.sway.mixin.PropertyChangeListenerMixin;

import javax.swing.JMenuBar;
import java.beans.PropertyChangeListener;


/**
 * Just a way to fluently construct menus, so you don't have to deal with all the intermediate variables.
 *
 * <pre>{@code
 *     SMenuBar.of(jFrame)
 *         .add(SMenu.of("menu1")
 *             .add(SMenuItem.of("menuitem 1a", this::menu1aHandler))
 *             .add(SMenuItem.of("menuitem 1b", this::menu1bHandler))
 *         )
 *         .add(SMenu.of("menu2")
 *             .add(SMenuItem.of("menuitem 2a", this::menu2aHandler))
 *             .add(SMenuItem.of("menuitem 2b", this::menu2bHandler))
 *             .add(SMenuItem.of("menuitem 2c", this::menu2cHandler))
 *         )
 *         .add(SMenu.of("menu3")
 *             .add(SMenuItem.of("menuitem 3a", this::menu3aHandler))
 *         );
 * }</pre>
 *
 * Or via the menuBar method on SFrame and SDialog:
 * <pre>{@code
 *     SFrame.of(panel)
 *           .menuBar(this::populateMenuBar)
 *           .visible(true);
 *     ...
 *     private void populateMenuBar(SMenuBar sMenuBar) {
 *         sMenuBar
 *             .add(SMenu.of("menu1")
 *                 .add(SMenuItem.of("menuitem 1a")
 *                 .add(SMenuItem.of("menuitem 1b")
 *             );
 *     }
 * }</pre>
 */
public class SMenuBar extends JMenuBar implements PropertyChangeListenerMixin<SMenuBar> {

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

    public SMenuBar withPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        super.addPropertyChangeListener(propertyName, listener);
        return this;
    }
    public SMenuBar withPropertyChangeListener(PropertyChangeListener listener) {
        super.addPropertyChangeListener(listener);
        return this;
    }
}

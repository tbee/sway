package org.tbee.sway;

import javax.swing.JComponent;
import javax.swing.JFrame;
import java.util.function.Consumer;

// TBEERNOT: constructor with title plus of methods, dito SDialog
// TBEERNOT Javadoc on how to use SFrame dito SDialog

public class SFrame extends JFrame implements SOverlay.OverlayProvider {

    public SFrame() {
        disposeOnClose();
        setGlassPane(new SOverlay());
    }

    // ===========================================================================
    // FACTORY

    static public SFrame of() {
        return new SFrame();
    }

    static public SFrame of(JComponent component) {
        SFrame sFrame = new SFrame();
        sFrame.setContentPane(component);
        return sFrame;
    }

    // ===========================================================================
    // FLUENT API

    public SFrame exitOnClose() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        return this;
    }
    public SFrame hideOnClose() {
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        return this;
    }
    public SFrame disposeOnClose() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        return this;
    }
    public SFrame doNothinhOnClose() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        return this;
    }

    public SFrame name(String v) {
        setName(v);
        return this;
    }

    public SFrame visible(boolean v) {
        setVisible(v);
        return this;
    }

    public SFrame size(int width, int height) {
        setSize(width, height);
        return this;
    }

    public SFrame sizeToPreferred() {
        pack();
        return this;
    }

    public SFrame maximize() {
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        return this;
    }

    public SFrame normal() {
        setExtendedState(JFrame.NORMAL);
        return this;
    }

    public SFrame iconify() {
        setExtendedState(JFrame.ICONIFIED);
        return this;
    }

    /**
     * Use this method like so:
     * <pre>{@code
     *     private void run() {
     *             SFrame.of(panel)
     *                     .menuBar(this::populateMenuBar)
     *                     .visible(true);
     *         });
     *     }
     *
     *     private void populateMenuBar(SMenuBar sMenuBar) {
     *         sMenuBar
     *             .add(SMenu.of("menu1")
     *                 .add(SMenuItem.of("menuitem 1a")
     *                 .add(SMenuItem.of("menuitem 1b")
     *             );
     *     }
     * }</pre>
     * @param sMenuBarConsumer
     * @return
     */
    public SFrame menuBar(Consumer<SMenuBar> sMenuBarConsumer) {
        SMenuBar sMenuBar = SMenuBar.of(this);
        sMenuBarConsumer.accept(sMenuBar);
        setJMenuBar(sMenuBar);
        return this;
    }
}

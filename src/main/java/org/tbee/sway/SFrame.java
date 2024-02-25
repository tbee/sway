package org.tbee.sway;

import org.tbee.sway.mixin.ComponentMixin;
import org.tbee.sway.mixin.KeyListenerMixin;

import javax.swing.JComponent;
import javax.swing.JFrame;
import java.awt.Image;
import java.util.function.Consumer;

// TBEERNOT: constructor with title plus of methods, dito SDialog
// TBEERNOT Javadoc on how to use SFrame dito SDialog

public class SFrame extends JFrame implements
        SOverlayPane.OverlayProvider,
        KeyListenerMixin<SFrame>,
        ComponentMixin<SFrame> {

    public SFrame() {
        disposeOnClose();
        setGlassPane(new SOverlayPane());
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

    public SFrame undecorated() {
        setUndecorated(true);
        return this;
    }

    public SFrame iconImage(Image image) {
        super.setIconImage(image);
        return this;
    }
    public SFrame iconImages(java.util.List<? extends Image> icons) {
        super.setIconImages(icons);
        return this;
    }

    public SFrame title(String title) {
        setTitle(title);
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

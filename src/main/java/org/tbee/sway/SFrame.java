package org.tbee.sway;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class SFrame extends JFrame {

    public void SFrame() {
        disposeOnClose();
    }

    // ===========================================================================
    // FACTORY

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
}

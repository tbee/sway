package org.tbee.sway;

import javax.swing.Icon;
import java.awt.event.ActionListener;

public class SMenuItem extends javax.swing.JMenuItem {

    // ===========================================================================================================================
    // FLUENT API

    public SMenuItem name(String v) {
        setName(v);
        return this;
    }

    public SMenuItem text(String value) {
        setText(value);
        return this;
    }

    public SMenuItem icon(Icon value) {
        setIcon(value);
        return this;
    }

    public SMenuItem enabled(boolean v) {
        super.setEnabled(v);
        return this;
    }

    public SMenuItem onAction(ActionListener v) {
        super.addActionListener(v);
        return this;
    }
}

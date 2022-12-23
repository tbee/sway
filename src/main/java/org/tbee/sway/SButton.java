package org.tbee.sway;

import javax.swing.*;

public class SButton extends JButton {

    public SButton() {
    }

    public SButton(Icon icon) {
        super(icon);
    }

    public SButton(String text) {
        super(text);
    }

    public SButton(Action a) {
        super(a);
    }

    public SButton(String text, Icon icon) {
        super(text, icon);
    }

    // ==============================================
    // FLUENT API

    /** */
    public SButton name(String v) {
        setName(v);
        return this;
    }
}

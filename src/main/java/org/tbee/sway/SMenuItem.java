package org.tbee.sway;

import java.awt.event.ActionListener;

import javax.swing.Icon;

public class SMenuItem extends javax.swing.JMenuItem {

    /**
	 * 
	 */
	public SMenuItem() {
		super();
	}

	/**
	 * @param icon
	 */
	public SMenuItem(Icon icon) {
		super(icon);
	}

	/**
	 * @param text
	 * @param icon
	 */
	public SMenuItem(String text, Icon icon) {
		super(text, icon);
	}

	/**
	 * @param text
	 */
	public SMenuItem(String text) {
		super(text);
	}

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

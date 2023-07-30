package org.tbee.sway;

import javax.swing.*;
import java.awt.event.ActionListener;

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

	static public SMenuItem of() {
		return new SMenuItem();
	}

	/**
	 * @param s
	 */
	static public SMenuItem of(String s) {
		return of().text(s);
	}

	/**
	 * @param icon
	 */
	static public SMenuItem of(Icon icon) {
		return of().icon(icon);
	}

	/**
	 * @param s
	 * @param icon
	 */
	static public SMenuItem of(String s, Icon icon) {
		return of().text(s).icon(icon);
	}

	/**
	 * @param s
	 */
	static public SMenuItem of(String s, ActionListener actionListener) {
		return of().text(s).onAction(actionListener);
	}

	/**
	 * @param icon
	 */
	static public SMenuItem of(Icon icon, ActionListener actionListener) {
		return of().icon(icon).onAction(actionListener);
	}

	/**
	 * @param s
	 * @param icon
	 */
	static public SMenuItem of(String s, Icon icon, ActionListener actionListener) {
		return of().text(s).icon(icon).onAction(actionListener);
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

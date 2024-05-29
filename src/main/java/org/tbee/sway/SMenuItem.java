package org.tbee.sway;

import org.tbee.sway.binding.BindingEndpoint;
import org.tbee.sway.mixin.JComponentMixin;
import org.tbee.sway.mixin.TextIconMixin;

import javax.swing.Icon;
import java.awt.event.ActionListener;

/**
 * @see SMenuBar
 */
public class SMenuItem extends javax.swing.JMenuItem implements
		TextIconMixin<SMenuItem>,
        JComponentMixin<SMenuItem> {

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
	// JavaBean

	/**
	 * Add PCE event
	 */
	public void setVisible(boolean v) {
		boolean old = super.isVisible();
		super.setVisible(v);
		firePropertyChange(VISIBLE, old, v);
	}

	/**
	 * Add PCE event
	 */
	public void setEnabled(boolean v) {
		boolean old = super.isEnabled();
		super.setEnabled(v);
		firePropertyChange(ENABLED, old, v);
	}

	/**
	 * Add PCE event
	 */
	public void setText(String v) {
		String old = super.getText();
		super.setText(v);
		firePropertyChange(TEXT, old, v);
	}

	/**
	 * Add PCE event
	 */
	public void setIcon(Icon v) {
		Icon old = super.getIcon();
		super.setIcon(v);
		firePropertyChange(ICON, old, v);
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

    public SMenuItem text(String value) {
        setText(value);
        return this;
    }

    public SMenuItem icon(Icon value) {
        setIcon(value);
        return this;
    }

    public SMenuItem onAction(ActionListener v) {
        super.addActionListener(v);
        return this;
    }
}

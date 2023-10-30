package org.tbee.sway;

import org.tbee.sway.binding.BindingEndpoint;

import javax.swing.Icon;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;

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
	// JavaBean

	/**
	 * Add PCE event
	 */
	public void setVisible(boolean v) {
		boolean old = super.isVisible();
		super.setVisible(v);
		firePropertyChange(VISIBLE, old, v);
	}
	final static public String VISIBLE = "visible";
	public BindingEndpoint<Boolean> visible$() {
		return BindingEndpoint.of(this, VISIBLE);
	}

	/**
	 * Add PCE event
	 */
	public void setEnabled(boolean v) {
		boolean old = super.isEnabled();
		super.setEnabled(v);
		firePropertyChange(ENABLED, old, v);
	}
	final static public String ENABLED = "enabled";
	public BindingEndpoint<Boolean> enabled$() {
		return BindingEndpoint.of(this, ENABLED);
	}

	/**
	 * Add PCE event
	 */
	public void setText(String v) {
		String old = super.getText();
		super.setText(v);
		firePropertyChange(TEXT, old, v);
	}
	final static public String TEXT = "text";
	public BindingEndpoint<String> text$() {
		return BindingEndpoint.of(this, TEXT);
	}

	/**
	 * Add PCE event
	 */
	public void setIcon(Icon v) {
		Icon old = super.getIcon();
		super.setIcon(v);
		firePropertyChange(TEXT, old, v);
	}
	final static public String ICON = "icon";
	public BindingEndpoint<Icon> ioon$() {
		return BindingEndpoint.of(this, ICON);
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

	public SMenuItem withPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		super.addPropertyChangeListener(propertyName, listener);
		return this;
	}
	public SMenuItem withPropertyChangeListener(PropertyChangeListener listener) {
		super.addPropertyChangeListener(listener);
		return this;
	}
}

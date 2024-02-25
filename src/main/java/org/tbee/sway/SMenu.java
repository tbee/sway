package org.tbee.sway;

import org.tbee.sway.binding.BindingEndpoint;
import org.tbee.sway.mixin.ComponentMixin;

import javax.swing.Icon;
import javax.swing.JMenu;
import java.awt.event.ActionListener;

/**
 * @see SMenuBar
 */
public class SMenu extends JMenu implements
		ComponentMixin<SMenu> {

	/**
	 * 
	 */
	public SMenu() {
		super();
	}

	/**
	 * @param s
	 */
	public SMenu(String s) {
		super(s);
	}

	/**
	 * @param icon
	 */
	public SMenu(Icon icon) {
		super("");
		icon(icon);
	}

	/**
	 * @param s
	 * @param icon
	 */
	public SMenu(String s, Icon icon) {
		super(s);
		icon(icon);
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

	static public SMenu of() {
		return new SMenu();
	}

	/**
	 * @param s
	 */
	static public SMenu of(String s) {
		return of().text(s);
	}

	/**
	 * @param icon
	 */
	static public SMenu of(Icon icon) {
		return of().icon(icon);
	}

	/**
	 * @param s
	 * @param icon
	 */
	static public SMenu of(String s, Icon icon) {
		return of().text(s).icon(icon);
	}

	public SMenu add(SMenuItem v) {
		super.add(v);
		return this;
	}

    public SMenu text(String value) {
        setText(value);
        return this;
    }

    public SMenu icon(Icon value) {
        setIcon(value);
        return this;
    }

    public SMenu onAction(ActionListener v) {
        super.addActionListener(v);
        return this;
    }
}

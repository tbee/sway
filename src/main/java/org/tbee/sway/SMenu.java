package org.tbee.sway;

import javax.swing.*;
import java.awt.event.ActionListener;

public class SMenu extends JMenu {

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

	public SMenu name(String v) {
        setName(v);
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

    public SMenu enabled(boolean v) {
        super.setEnabled(v);
        return this;
    }

    public SMenu onAction(ActionListener v) {
        super.addActionListener(v);
        return this;
    }
}

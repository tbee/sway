package org.tbee.sway;

import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JMenu;

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
	 * @param s
	 */
	public SMenu(Icon icon) {
		super("");
		icon(icon);
	}

	/**
	 * @param s
	 */
	public SMenu(String s, Icon icon) {
		super(s);
		icon(icon);
	}


	// ===========================================================================================================================
    // FLUENT API

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

package org.tbee.sway;

import javax.swing.Icon;
import javax.swing.JRadioButtonMenuItem;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;

public class SRadioButtonMenuItem extends JRadioButtonMenuItem {

	/**
	 * 
	 */
	public SRadioButtonMenuItem() {
		super();
	}

	/**
	 * @param icon
	 */
	public SRadioButtonMenuItem(Icon icon) {
		super(icon);
	}

	/**
	 * @param text
	 * @param icon
	 */
	public SRadioButtonMenuItem(String text, Icon icon) {
		super(text, icon);
	}

	/**
	 * @param text
	 */
	public SRadioButtonMenuItem(String text) {
		super(text);
	}

    // ===========================================================================================================================
    // FLUENT API

    public SRadioButtonMenuItem name(String v) {
        setName(v);
        return this;
    }

    public SRadioButtonMenuItem text(String value) {
        setText(value);
        return this;
    }

    public SRadioButtonMenuItem icon(Icon value) {
        setIcon(value);
        return this;
    }

    public SRadioButtonMenuItem enabled(boolean v) {
        super.setEnabled(v);
        return this;
    }

    public SRadioButtonMenuItem onAction(ActionListener v) {
        super.addActionListener(v);
        return this;
    }

	public SRadioButtonMenuItem withPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		super.addPropertyChangeListener(propertyName, listener);
		return this;
	}
	public SRadioButtonMenuItem withPropertyChangeListener(PropertyChangeListener listener) {
		super.addPropertyChangeListener(listener);
		return this;
	}
}

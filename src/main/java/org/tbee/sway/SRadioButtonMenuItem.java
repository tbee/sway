package org.tbee.sway;

import org.tbee.sway.mixin.ComponentMixin;

import javax.swing.Icon;
import javax.swing.JRadioButtonMenuItem;
import java.awt.event.ActionListener;

public class SRadioButtonMenuItem extends JRadioButtonMenuItem implements
		ComponentMixin<SRadioButtonMenuItem> {

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

    public SRadioButtonMenuItem text(String value) {
        setText(value);
        return this;
    }

    public SRadioButtonMenuItem icon(Icon value) {
        setIcon(value);
        return this;
    }

    public SRadioButtonMenuItem onAction(ActionListener v) {
        super.addActionListener(v);
        return this;
    }
}

package org.tbee.sway;

import java.util.Collection;

import javax.swing.AbstractButton;
import javax.swing.JComponent;

// TBEERNOT this class needs just a bit more work
public class SButtonPanel extends SMigPanel {

	/**
	 * 
	 */
	public SButtonPanel() {
		super();
	}

	/**
	 * @param components
	 */
	public SButtonPanel(Collection<? extends JComponent> components) {
		super(components);
	}

	/**
	 * @param components
	 */
	public SButtonPanel(JComponent... components) {
		super(components);
	}

	static SButtonPanel of(AbstractButton... buttons) {
		return new SButtonPanel(buttons);
	}
}

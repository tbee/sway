package org.tbee.sway;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Window;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

public class SDialog extends JDialog {

	
	/**
	 * @param owner
	 * @param title
	 * @param modalityType
	 */
	public SDialog(Window owner, String title, ModalityType modalityType) {
		super(owner, title, modalityType);
		setDefaultCloseOperation(SDialog.DISPOSE_ON_CLOSE);		
	}

	static public SDialog ofCancel(Component parent, String title, JComponent content) {
		var dialog = new SDialog(parent == null ? null : SwingUtilities.windowForComponent(parent), title, Dialog.ModalityType.DOCUMENT_MODAL);
		var cancelButton = SOptionPane.cancelButton();
		dialog.setContentPane(SBorderPanel.of(content).south(SButtonPanel.of(cancelButton)));
		cancelButton.onAction(e -> dialog.setVisible(false));
		dialog.pack();
		return dialog;
	}
	
	public SDialog visible(boolean v) {
		setVisible(v);
		return this;
	}
	
	public SDialog showAndWait() {
		setVisible(true);
		return this;
	}
}

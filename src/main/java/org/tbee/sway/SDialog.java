package org.tbee.sway;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Window;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

public class SDialog extends JDialog {

	/*
	 * The reason why a dialog is closed
	 */
	private enum CloseReason {CANCEL, OK}
	private CloseReason closeReason;
	private SDialog closeReason(CloseReason v) {
		this.closeReason = v;
		return this;
	}
	public boolean closeReasonIsUnknown() {
		return closeReason == null;
	}
	public boolean closeReasonIsOk() {
		return closeReason != null && closeReason == CloseReason.OK;
	}
	public boolean closeReasonIsCancel() {
		return closeReason != null && closeReason == CloseReason.OK;
	}
	
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
		return of(parent, title, content, CloseReason.CANCEL);
	}
	
	static public SDialog ofOkCancel(Component parent, String title, JComponent content) {
		return of(parent, title, content, CloseReason.OK, CloseReason.CANCEL);
	}
	
	static private SDialog of(Component parent, String title, JComponent content, CloseReason... closeReasons) {
		
		// Create dialog
		var dialog = new SDialog(parent == null ? null : SwingUtilities.windowForComponent(parent), title, Dialog.ModalityType.DOCUMENT_MODAL);
		
		// Create buttons
		var buttons = new ArrayList<SButton>();
		for (CloseReason closeReason : closeReasons) {
			var button = switch (closeReason) {
				case OK -> SOptionPane.okButton();
				case CANCEL -> SOptionPane.cancelButton();
			};
			button.onAction(e -> dialog.closeReason(closeReason).setVisible(false)) // hide dialog upon click
				.registerKeyboardAction(e -> button.doClick() //
					, KeyStroke.getKeyStroke(button.getText().toUpperCase().charAt(0), 0) //
					, JButton.WHEN_IN_FOCUSED_WINDOW //
				);
			buttons.add(button);
		}
		
		// Populate and show dialog
		dialog.setContentPane(SBorderPanel.of(content).south(SButtonPanel.of(buttons)));
		dialog.pack();
		return dialog;
	}
	
	public SDialog visible(boolean v) {
		setVisible(v);
		return this;
	}
	
	public SDialog centerOnScreen() {
		SwingUtil.centerOnScreen(this);
		return this;
	}
	
	public SDialog showAndWait() {
		setVisible(true);
		return this;
	}
}

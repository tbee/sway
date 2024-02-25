package org.tbee.sway;

import org.tbee.sway.mixin.PropertyChangeListenerMixin;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Window;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.function.Consumer;

public class SDialog extends JDialog implements SOverlayPane.OverlayProvider, PropertyChangeListenerMixin<SDialog> {
	
	/**
	 * @param owner
	 * @param title
	 * @param modalityType
	 */
	public SDialog(Window owner, String title, ModalityType modalityType) {
		super(owner, title, modalityType);
		setDefaultCloseOperation(SDialog.DISPOSE_ON_CLOSE);
		setGlassPane(new SOverlayPane());
	}


	// =======================================================================================================
	// PROPERTIES
	
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

	// =======================================================================================================
	// CONVENIENCE
	
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
			button.onAction(e -> dialog.closeReason(closeReason).visible(false)) // hide dialog upon click
				.registerKeyboardAction(e -> button.doClick() //
					, KeyStroke.getKeyStroke(button.getText().toUpperCase().charAt(0), 0) //
					, JButton.WHEN_IN_FOCUSED_WINDOW //
				);
			buttons.add(button);
		}
		
		// Populate and show dialog
		dialog.setContentPane(SBorderPanel.of(content).south(SButtonPanel.of(buttons)).margin(4));
		dialog.pack();
		if (parent != null) {
			dialog.setLocationRelativeTo(parent);
		}
		return dialog;
	}


	// =======================================================================================================
	// FLUENT API
	
    public SDialog name(String v) {
        setName(v);
        return this;
    }

	public SDialog visible(boolean v) {
		setVisible(v);
		return this;
	}

	public SDialog size(int width, int height) {
		setSize(width, height);
		return this;
	}

	public SDialog sizeToPreferred() {
		pack();
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

	/**
	 * Use this method like so:
	 * <pre>{@code
	 *     private void run() {
	 *             SDialog.of(panel)
	 *                     .menuBar(this::populateMenuBar)
	 *                     .visible(true);
	 *         });
	 *     }
	 *
	 *     private void populateMenuBar(SMenuBar sMenuBar) {
	 *         sMenuBar
	 *             .add(SMenu.of("menu1")
	 *                 .add(SMenuItem.of("menuitem 1a")
	 *                 .add(SMenuItem.of("menuitem 1b")
	 *             );
	 *     }
	 * }</pre>
	 * @param sMenuBarConsumer
	 * @return
	 */
	public SDialog menuBar(Consumer<SMenuBar> sMenuBarConsumer) {
		SMenuBar sMenuBar = SMenuBar.of(this);
		sMenuBarConsumer.accept(sMenuBar);
		setJMenuBar(sMenuBar);
		return this;
	}

	public SDialog withPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		super.addPropertyChangeListener(propertyName, listener);
		return this;
	}
	public SDialog withPropertyChangeListener(PropertyChangeListener listener) {
		super.addPropertyChangeListener(listener);
		return this;
	}
}

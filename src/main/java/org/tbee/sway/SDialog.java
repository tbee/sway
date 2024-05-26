package org.tbee.sway;

import org.tbee.sway.mixin.ComponentMixin;
import org.tbee.sway.mixin.WindowMixin;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Window;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SDialog extends JDialog implements
		SOverlayPane.OverlayProvider,
		WindowMixin<SDialog>,
		ComponentMixin<SDialog> {

	private Supplier<Boolean> onCancelCallback = () -> true;
	private Supplier<Boolean> onOkCallback = () -> true;

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
	private Boolean closeReason(CloseReason v) {
		var closeOk = switch (v) {
			case OK -> onOkCallback.get();
			case CANCEL -> onCancelCallback.get();
		};
		if (!closeOk) {
			return false;
		}

		this.closeReason = v;
		return true;
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

	public SDialog onCancel(Supplier<Boolean> callback) {
		if (callback == null) {
			throw new IllegalArgumentException("Callback cannot be null");
		}
		onCancelCallback = callback;
		return this;
	}
	public SDialog onCancel(Runnable callback) {
		if (callback == null) {
			throw new IllegalArgumentException("Callback cannot be null");
		}
		return onCancel(() -> {
			callback.run();
			return true;
		});
	}

	public SDialog onOk(Supplier<Boolean> callback) {
		if (callback == null) {
			throw new IllegalArgumentException("Callback cannot be null");
		}
		onOkCallback = callback;
		return this;
	}
	public SDialog onOk(Runnable callback) {
		if (callback == null) {
			throw new IllegalArgumentException("Callback cannot be null");
		}
		return onOk(() -> {
			callback.run();
			return true;
		});
	}

	// =======================================================================================================
	// CONVENIENCE

	static public SDialog of(Component parent, String title, JComponent content) {
		return of(parent, title, content, new CloseReason[]{});
	}

	static public SDialog ofCancel(Component parent, String title, JComponent content) {
		return of(parent, title, content, CloseReason.CANCEL);
	}

	static public SDialog ofOk(Component parent, String title, JComponent content) {
		return of(parent, title, content, CloseReason.OK);
	}

	static public SDialog ofOkCancel(Component parent, String title, JComponent content) {
		return of(parent, title, content, CloseReason.OK, CloseReason.CANCEL);
	}
	
	static private SDialog of(Component parent, String title, JComponent content, CloseReason... closeReasons) {
		
		// Create dialog
		Window window = (parent instanceof Window parentWindow ? parentWindow : SwingUtilities.windowForComponent(parent));
		var dialog = new SDialog(parent == null ? null : window, title, Dialog.ModalityType.DOCUMENT_MODAL);
		
		// Create buttons
		var buttons = new ArrayList<SButton>();
		for (CloseReason closeReason : closeReasons) {
			var button = switch (closeReason) {
				case OK -> SOptionPane.okButton();
				case CANCEL -> SOptionPane.cancelButton();
			};
			button.onAction(e -> {
					if (dialog.closeReason(closeReason)) {
						dialog.visible(false);
					}
				}) // hide dialog upon click
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
	
	public SDialog size(int w, int h) {
		setSize(w, h);
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
}

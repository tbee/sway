package org.tbee.sway;

import org.checkerframework.checker.units.qual.C;
import org.tbee.sway.mixin.ComponentMixin;
import org.tbee.sway.mixin.WindowMixin;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Supplier;

public class SConfirmDialog extends JDialog implements
		SOverlayPane.OverlayProvider,
		WindowMixin<SConfirmDialog>,
		ComponentMixin<SConfirmDialog> {

    private JComponent content;
	private String cancelText = "Cancel";
    private Supplier<Boolean> onCancelCallback = null;
	private String okText = "OK";
	private Supplier<Boolean> onOkCallback = null;

	/**
	 * @param owner
	 * @param title
	 * @param modalityType
	 */
	public SConfirmDialog(Window owner, String title, ModalityType modalityType, JComponent content) {
		super(owner, title, modalityType);
        this.content = content;
        setDefaultCloseOperation(SConfirmDialog.DISPOSE_ON_CLOSE);
		setGlassPane(new SOverlayPane());
	}


	// =======================================================================================================
	// PROPERTIES
	
	public enum CloseReason {
		CANCEL, OK;
		public boolean isCancel() {
			return this == CANCEL;
		}
		public boolean isOk() {
			return this == OK;
		}
	}
	private CloseReason closeReason = CloseReason.CANCEL; // This is the dialog close button

	public SConfirmDialog cancelText(String v) {
		cancelText = v;
		return this;
	}
	public SConfirmDialog onCancel(Supplier<Boolean> callback) {
		Objects.requireNonNull(callback, "Callback cannot be null");
		onCancelCallback = callback;
		return this;
	}
	public SConfirmDialog onCancel(Runnable callback) {
		Objects.requireNonNull(callback, "Callback cannot be null");
		return onCancel(() -> {
			callback.run();
			return true;
		});
	}
	public SConfirmDialog onCancelJustClose() {
		return onCancel(() -> {});
	}

	public SConfirmDialog okText(String v) {
		okText = v;
		return this;
	}
	public SConfirmDialog onOk(Supplier<Boolean> callback) {
		Objects.requireNonNull(callback, "Callback cannot be null");
		onOkCallback = callback;
		return this;
	}
	public SConfirmDialog onOk(Runnable callback) {
		Objects.requireNonNull(callback, "Callback cannot be null");
		return onOk(() -> {
			callback.run();
			return true;
		});
	}
	public SConfirmDialog onOkJustClose() {
		return onOk(() -> {});
	}

	public CloseReason showAndWait() {
		construct();
		setVisible(true); // This block
		return closeReason;
	}

	public SConfirmDialog construct() {
		// Create buttons
		var buttons = new ArrayList<SButton>();
		if (onCancelCallback != null) {
			buttons.add(createButton(cancelText, CloseReason.CANCEL, onCancelCallback));
		}
		if (onOkCallback != null) {
			buttons.add(createButton(okText, CloseReason.OK, onOkCallback));
		}

		// Populate and show dialog
		setContentPane(SBorderPanel.of(content).south(SButtonPanel.of(buttons).margin(5, 0, 0, 0)).margin(4));
		pack();
		if (getOwner() != null) {
			setLocationRelativeTo(getOwner());
		}
		return this;
	}

	private SButton createButton(String text, CloseReason closeReason, Supplier<Boolean> callback) {
		SButton sButton = SButton.of(text)
				.onAction(e -> {
					SConfirmDialog.this.closeReason = closeReason;
					if (callback.get()) {
						close();
					}
				});
		sButton.registerKeyboardAction(e -> sButton.doClick() //
				, KeyStroke.getKeyStroke(text.toUpperCase().charAt(0), 0) //
				, JButton.WHEN_IN_FOCUSED_WINDOW //
		);
		getRootPane().setDefaultButton(sButton); // The last button is the default button
		return sButton;
	}

	// =======================================================================================================
	// CONVENIENCE

	static public SConfirmDialog of(Component parent, String title, JComponent content) {
		return new SConfirmDialog(parent instanceof Window window ? window : SwingUtilities.windowForComponent(parent), title, ModalityType.DOCUMENT_MODAL, content);
	}


	// =======================================================================================================
	// FLUENT API

	public SConfirmDialog content(JComponent v) {
		this.content = v;
		return this;
	}

	public SConfirmDialog size(int w, int h) {
		setSize(w, h);
		return this;
	}

	public SConfirmDialog sizeToPreferred() {
		pack();
		return this;
	}

	public SConfirmDialog centerOnScreen() {
		SwingUtil.centerOnScreen(this);
		return this;
	}

	public SConfirmDialog close() {
		setVisible(false);
		return this;
	}

	public SConfirmDialog noWindowDecoration() {
		getRootPane().setWindowDecorationStyle(JRootPane.NONE);
		return this;
	}

	public SConfirmDialog contentPane(Container contentPane) {
		setContentPane(contentPane);
		return this;
	}
}

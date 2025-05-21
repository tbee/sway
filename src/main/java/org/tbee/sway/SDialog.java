package org.tbee.sway;

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
import java.awt.Dialog;
import java.awt.Window;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

/// Usage:
///  SDialog sDialog = SDialog.of(owner, "Pick me", datePicker);
public class SDialog extends JDialog implements
		SOverlayPane.OverlayProvider,
		WindowMixin<SDialog>,
		ComponentMixin<SDialog> {

	public SDialog(Component owner) {
		this(owner, "", ModalityType.DOCUMENT_MODAL);
	}

	public SDialog(Component owner, String title, ModalityType modalityType) {
		super(owner instanceof Window window ? window : SwingUtilities.windowForComponent(owner), title, modalityType);
		setDefaultCloseOperation(SDialog.DISPOSE_ON_CLOSE);
		setGlassPane(new SOverlayPane());
	}


	// ===========================================================================
	// FACTORY

	static public SDialog of(Component owner) {
		return new SDialog(owner);
	}

	static public SDialog of(Component owner, JComponent component) {
		return of(owner, "", component);
	}

	static public SDialog of(Component owner, String title, JComponent component) {
		SDialog sDialog = new SDialog(owner, title, ModalityType.DOCUMENT_MODAL);
		sDialog.setContentPane(component);
		return sDialog;
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

	public SDialog close() {
		setVisible(false);
		return this;
	}

	public SDialog noWindowDecoration() {
		getRootPane().setWindowDecorationStyle(JRootPane.NONE);
		return this;
	}

	public SDialog contentPane(Container contentPane) {
		setContentPane(contentPane);
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

package org.tbee.sway;

import java.awt.Component;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

public class SOptionPane extends JOptionPane {

	static public void ofOk(Component parent, String title, String text) {
		SOptionPane.showConfirmDialog(parent, text, title, JOptionPane.OK_OPTION, JOptionPane.QUESTION_MESSAGE);
	}
	
	static public void ofOk(Component parent, String title, JComponent content) {
		
		var okButton = okButton();		
		JOptionPane optionPane = createOptionPane(title, JOptionPane.QUESTION_MESSAGE, okButton);		
		addHotkeys(optionPane, okButton);
		showAndWait(parent, title, optionPane);
	}

	private static JOptionPane createOptionPane(String title, int messageType, SButton... buttons) {
		JOptionPane optionPane = new JOptionPane(title, messageType, JOptionPane.DEFAULT_OPTION, null, buttons);
		return optionPane;
	}

	private static Object showAndWait(Component parent, String title, JOptionPane optionPane) {
		JDialog dialog = optionPane.createDialog(parent, title);
		dialog.setVisible(true);
		return optionPane.getValue();
	}
	
	private static void addHotkeys(JOptionPane optionPane, SButton... buttons) {
		for (SButton button : buttons) {
			button.onAction(e -> optionPane.setValue(e.getSource())) //
			.registerKeyboardAction(e -> button.doClick() //
				, KeyStroke.getKeyStroke(button.getText().toUpperCase().charAt(0), 0) //
				, JButton.WHEN_IN_FOCUSED_WINDOW //
			);
		}
	}

	static SButton okButton() {
		return new SButton(UIManager.getString("OptionPane.okButtonText", Locale.getDefault()));
	}

	static SButton cancelButton() {
		return new SButton(UIManager.getString("OptionPane.cancelButtonText", Locale.getDefault()));
	}

	static SButton yesButton() {
		return new SButton(UIManager.getString("OptionPane.yesButtonText", Locale.getDefault()));
	}

	static SButton noButton() {
		return new SButton(UIManager.getString("OptionPane.noButtonText", Locale.getDefault()));
	}
}

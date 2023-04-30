package org.tbee.sway;

import org.tbee.sway.support.IconRegistry;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import java.awt.Component;
import java.util.Locale;

public class SOptionPane extends JOptionPane {

	static public void ofInfo(Component parent, String title, String text) {
		SOptionPane.showMessageDialog(parent, text, title, JOptionPane.INFORMATION_MESSAGE);
	}
	
	static SButton okButton() {
		return SButton.of(UIManager.getString("OptionPane.okButtonText", Locale.getDefault()), IconRegistry.find(IconRegistry.SwayInternallyUsedIcon.BUTTON_OK));
	}

	static SButton cancelButton() {
		return SButton.of(UIManager.getString("OptionPane.cancelButtonText", Locale.getDefault()), IconRegistry.find(IconRegistry.SwayInternallyUsedIcon.BUTTON_CANCEL));
	}

	static SButton yesButton() {
		return SButton.of(UIManager.getString("OptionPane.yesButtonText", Locale.getDefault()), IconRegistry.find(IconRegistry.SwayInternallyUsedIcon.BUTTON_YES));
	}

	static SButton noButton() {
		return SButton.of(UIManager.getString("OptionPane.noButtonText", Locale.getDefault()), IconRegistry.find(IconRegistry.SwayInternallyUsedIcon.BUTTON_NO));
	}
}

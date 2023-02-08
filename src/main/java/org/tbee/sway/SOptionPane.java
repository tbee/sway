package org.tbee.sway;

import java.awt.Component;
import java.util.Locale;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.tbee.sway.support.IconRegistry;

public class SOptionPane extends JOptionPane {

	static public void ofInfo(Component parent, String title, String text) {
		SOptionPane.showMessageDialog(parent, text, title, JOptionPane.INFORMATION_MESSAGE);
	}
	
	static SButton okButton() {
		return new SButton(UIManager.getString("OptionPane.okButtonText", Locale.getDefault()), IconRegistry.find(IconRegistry.SwayInternallyUsedIcon.BUTTON_OK));
	}

	static SButton cancelButton() {
		return new SButton(UIManager.getString("OptionPane.cancelButtonText", Locale.getDefault()), IconRegistry.find(IconRegistry.SwayInternallyUsedIcon.BUTTON_CANCEL));
	}

	static SButton yesButton() {
		return new SButton(UIManager.getString("OptionPane.yesButtonText", Locale.getDefault()), IconRegistry.find(IconRegistry.SwayInternallyUsedIcon.BUTTON_YES));
	}

	static SButton noButton() {
		return new SButton(UIManager.getString("OptionPane.noButtonText", Locale.getDefault()), IconRegistry.find(IconRegistry.SwayInternallyUsedIcon.BUTTON_NO));
	}
}

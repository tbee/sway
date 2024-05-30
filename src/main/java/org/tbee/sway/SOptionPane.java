package org.tbee.sway;

import org.tbee.sway.mixin.PropertyChangeListenerMixin;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import java.awt.Component;
import java.util.Locale;

public class SOptionPane extends JOptionPane implements PropertyChangeListenerMixin<SOptionPane> {

	static public void ofInfo(Component parent, String title, String text) {
		SOptionPane.showMessageDialog(parent, text, title, JOptionPane.INFORMATION_MESSAGE);
	}

	static public void ofWarning(Component parent, String title, String text) {
		SOptionPane.showMessageDialog(parent, text, title, JOptionPane.WARNING_MESSAGE);
	}

	static public void ofError(Component parent, String title, String text) {
		SOptionPane.showMessageDialog(parent, text, title, JOptionPane.ERROR_MESSAGE);
	}

	static SButton okButton() {
		return SButton.of(UIManager.getString("OptionPane.okButtonText", Locale.getDefault()), SIconRegistry.find(SIconRegistry.SwayInternallyUsedIcon.BUTTON_OK));
	}

	static SButton cancelButton() {
		return SButton.of(UIManager.getString("OptionPane.cancelButtonText", Locale.getDefault()), SIconRegistry.find(SIconRegistry.SwayInternallyUsedIcon.BUTTON_CANCEL));
	}

	static SButton yesButton() {
		return SButton.of(UIManager.getString("OptionPane.yesButtonText", Locale.getDefault()), SIconRegistry.find(SIconRegistry.SwayInternallyUsedIcon.BUTTON_YES));
	}

	static SButton noButton() {
		return SButton.of(UIManager.getString("OptionPane.noButtonText", Locale.getDefault()), SIconRegistry.find(SIconRegistry.SwayInternallyUsedIcon.BUTTON_NO));
	}
}

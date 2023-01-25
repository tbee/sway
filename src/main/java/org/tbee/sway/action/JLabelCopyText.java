package org.tbee.sway.action;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JLabel;

import org.tbee.sway.support.IconRegistry;

public class JLabelCopyText implements Action {

    @Override
    public String label() {
        return "Copy";
    }

    @Override
    public Icon icon() {
        return IconRegistry.find(IconRegistry.SwayInternallyUsedIcon.MENU_COPY);
    }

    @Override
    public boolean isApplicableFor(Component component, Map<String, Object> context) {
        return component instanceof JLabel;
    }

    @Override
    public boolean isEnabled(Component component, Map<String, Object> context) {
        JLabel jLabel = (JLabel)component;
        return jLabel.getText() != null;
    }

    @Override
    public void apply(Component component, String option, Map<String, Object> context) {
        JLabel jLabel = (JLabel)component;
        String s = jLabel.getText();
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection stringSelection = new StringSelection(s);
        clipboard.setContents(stringSelection, stringSelection);
    }
}

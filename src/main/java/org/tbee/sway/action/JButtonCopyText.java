package org.tbee.sway.action;

import org.tbee.sway.support.IconRegistry;

import javax.swing.Icon;
import javax.swing.JButton;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.Map;

public class JButtonCopyText implements Action {

    @Override
    public String label() {
        return "Copy text";
    }

    @Override
    public Icon icon() {
        return IconRegistry.find("copy", IconRegistry.Usage.MENU);
    }

    @Override
    public boolean isApplicableFor(Component component, Map<String, Object> context) {
        return component instanceof JButton;
    }

    @Override
    public boolean isEnabled(Component component, Map<String, Object> context) {
        JButton jButton = (JButton)component;
        return jButton.getText() != null;
    }

    @Override
    public void apply(Component component, Map<String, Object> context) {
        JButton jButton = (JButton)component;
        String s = jButton.getText();
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection stringSelection = new StringSelection(s);
        clipboard.setContents(stringSelection, stringSelection);
    }
}

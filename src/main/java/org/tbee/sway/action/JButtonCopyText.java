package org.tbee.sway.action;

import javax.swing.Icon;
import javax.swing.JButton;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.Map;

public class JButtonCopyText implements Action, ClipboardOwner {

    @Override
    public String label() {
        return "Copy text";
    }

    @Override
    public Icon icon() {
        return null;
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
        clipboard.setContents(new StringSelection(s), this);
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        // do we need to do something here?
    }
}

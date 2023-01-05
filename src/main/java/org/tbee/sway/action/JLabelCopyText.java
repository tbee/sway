package org.tbee.sway.action;

import org.tbee.sway.support.IconRegistry;

import javax.swing.Icon;
import javax.swing.JLabel;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.Map;

public class JLabelCopyText implements Action, ClipboardOwner {

    @Override
    public String label() {
        return "Copy";
    }

    @Override
    public Icon icon() {
        return IconRegistry.find("copy", IconRegistry.Usage.MENU);
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
    public void apply(Component component, Map<String, Object> context) {
        JLabel jLabel = (JLabel)component;
        String s = jLabel.getText();
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(s), this);
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        // do we need to do something here?
    }
}

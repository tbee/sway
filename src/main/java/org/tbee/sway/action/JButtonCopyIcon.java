package org.tbee.sway.action;

import org.tbee.sway.transferable.ImageSelection;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.util.Map;

public class JButtonCopyIcon implements Action, ClipboardOwner {

    @Override
    public String label() {
        return "Copy icon";
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
        return jButton.getIcon() != null && jButton.getIcon() instanceof ImageIcon;
    }

    @Override
    public void apply(Component component, Map<String, Object> context) {
        JButton jButton = (JButton)component;
        ImageIcon icon = (ImageIcon)jButton.getIcon();
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new ImageSelection(icon.getImage()), this);
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        // do we need to do something here?
    }
}

package org.tbee.sway.action;

import org.tbee.sway.transferable.ImageSelection;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;

public class JLabelCopyIcon implements Action, ClipboardOwner {

    @Override
    public String label() {
        return "Copy icon";
    }

    @Override
    public Icon icon() {
        return null;
    }

    @Override
    public boolean isApplicableFor(Component component) {
        return component instanceof JLabel;
    }

    @Override
    public boolean isEnabled(Component component) {
        JLabel jLabel = (JLabel)component;
        return jLabel.getIcon() != null && jLabel.getIcon() instanceof ImageIcon;
    }

    @Override
    public void apply(Component component) {
        JLabel jLabel = (JLabel)component;
        ImageIcon icon = (ImageIcon)jLabel.getIcon();
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new ImageSelection(icon.getImage()), this);
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        // do we need to do something here?
    }
}

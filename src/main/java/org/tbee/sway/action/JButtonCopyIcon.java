package org.tbee.sway.action;

import org.tbee.sway.transferable.ImageSelection;

import javax.swing.Icon;
import javax.swing.JButton;
import java.awt.Component;
import java.awt.Image;
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
        return jButton.getIcon() != null;
    }

    @Override
    public void apply(Component component, Map<String, Object> context) {
        JButton jButton = (JButton)component;
        Image image = JLabelCopyIcon.convertIconToImage(jButton.getIcon());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new ImageSelection(image), this);
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        // do we need to do something here?
    }
}
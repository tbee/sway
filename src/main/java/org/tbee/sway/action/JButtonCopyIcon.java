package org.tbee.sway.action;

import org.tbee.sway.SIconRegistry;
import org.tbee.sway.transferable.ImageSelection;

import javax.swing.Icon;
import javax.swing.JButton;
import java.awt.Component;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.util.Map;

public class JButtonCopyIcon implements Action {

    @Override
    public String label() {
        return "Copy icon";
    }

    @Override
    public Icon icon() {
        return SIconRegistry.find(SIconRegistry.SwayInternallyUsedIcon.MENU_COPY);
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
    public void apply(Component component, String option, Map<String, Object> context) {
        JButton jButton = (JButton)component;
        Image image = JLabelCopyIcon.convertIconToImage(jButton.getIcon());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        ImageSelection imageSelection = new ImageSelection(image);
        clipboard.setContents(imageSelection, imageSelection);
    }
}

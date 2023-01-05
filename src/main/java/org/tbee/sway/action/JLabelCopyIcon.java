package org.tbee.sway.action;

import org.tbee.sway.support.IconRegistry;
import org.tbee.sway.transferable.ImageSelection;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.Component;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.util.Map;

public class JLabelCopyIcon implements Action, ClipboardOwner {

    @Override
    public String label() {
        return "Copy icon";
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
        return jLabel.getIcon() != null;
    }

    @Override
    public void apply(Component component, Map<String, Object> context) {
        JLabel jLabel = (JLabel)component;

        Image image = convertIconToImage(jLabel.getIcon());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new ImageSelection(image), this);
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        // do we need to do something here?
    }

    static Image convertIconToImage(Icon icon) {
        if (icon instanceof ImageIcon imageIcon) {
            return imageIcon.getImage();
        }
        BufferedImage bufferedImage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        icon.paintIcon(null, bufferedImage.getGraphics(), 0, 0);
        return bufferedImage;
    }
}

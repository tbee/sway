package org.tbee.sway.action;

import org.tbee.sway.SIconRegistry;
import org.tbee.sway.transferable.ImageSelection;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.Component;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.image.BufferedImage;
import java.util.Map;

public class JLabelCopyIcon implements Action {

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
        return component instanceof JLabel;
    }

    @Override
    public boolean isEnabled(Component component, Map<String, Object> context) {
        JLabel jLabel = (JLabel)component;
        return jLabel.getIcon() != null;
    }

    @Override
    public void apply(Component component, String option, Map<String, Object> context) {
        JLabel jLabel = (JLabel)component;

        Image image = convertIconToImage(jLabel.getIcon());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        ImageSelection imageSelection = new ImageSelection(image);
        clipboard.setContents(imageSelection, imageSelection);
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

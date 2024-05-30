package org.tbee.sway.action;

import org.tbee.sway.SIconRegistry;

import javax.swing.Icon;
import javax.swing.JLabel;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.Map;

public class JLabelCopyText implements Action {

    @Override
    public String label() {
        return "Copy";
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

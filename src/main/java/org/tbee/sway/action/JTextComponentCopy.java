package org.tbee.sway.action;

import org.tbee.sway.support.IconRegistry;

import javax.swing.Icon;
import javax.swing.text.JTextComponent;
import java.awt.Component;
import java.util.Map;

public class JTextComponentCopy implements Action {

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
        return component instanceof JTextComponent;
    }

    @Override
    public void apply(Component component, Map<String, Object> context) {
        JTextComponent jTextComponent = (JTextComponent)component;

        boolean enabled = jTextComponent.isEnabled();
        try {
            jTextComponent.setEnabled(true);
            jTextComponent.selectAll(); // TextComponent loses selection if a popup is shown
            jTextComponent.copy();
        }
        finally {
            jTextComponent.setEnabled(enabled);
        }
    }
}

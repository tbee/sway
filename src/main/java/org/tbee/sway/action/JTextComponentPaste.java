package org.tbee.sway.action;

import org.tbee.sway.SIconRegistry;

import javax.swing.Icon;
import javax.swing.text.JTextComponent;
import java.awt.Component;
import java.util.Map;

public class JTextComponentPaste implements Action {

    @Override
    public String label() {
        return "Paste";
    }

    @Override
    public Icon icon() {
        return SIconRegistry.find(SIconRegistry.SwayInternallyUsedIcon.MENU_PASTE);
    }

    @Override
    public boolean isApplicableFor(Component component, Map<String, Object> context) {
        return component instanceof JTextComponent;
    }

    @Override
    public boolean isEnabled(Component component, Map<String, Object> context) {
        JTextComponent jTextComponent = (JTextComponent)component;
        return jTextComponent.isEnabled() && jTextComponent.isEditable();
    }

    @Override
    public void apply(Component component, String option, Map<String, Object> context) {
        JTextComponent jTextComponent = (JTextComponent)component;
        jTextComponent.selectAll();
        jTextComponent.paste();
    }
}

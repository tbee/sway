package org.tbee.sway.action;

import java.awt.Component;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.text.JTextComponent;

import org.tbee.sway.support.IconRegistry;

public class JTextComponentCut implements Action {

    @Override
    public String label() {
        return "Cut";
    }

    @Override
    public Icon icon() {
        return IconRegistry.find(IconRegistry.SwayInternallyUsedIcon.MENU_CUT);
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
        jTextComponent.selectAll(); // TextComponent loses selection if a popup is shown
        jTextComponent.cut();
    }
}

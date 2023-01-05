package org.tbee.sway.action;

import javax.swing.Icon;
import javax.swing.text.JTextComponent;
import java.awt.Component;

public class JTextComponentPaste implements Action {

    @Override
    public String label() {
        return "Paste";
    }

    @Override
    public Icon icon() {
        return null;
    }

    @Override
    public boolean isApplicableFor(Component component) {
        return component instanceof JTextComponent;
    }

    @Override
    public boolean isEnabled(Component component) {
        JTextComponent jTextComponent = (JTextComponent)component;
        return jTextComponent.isEnabled();
    }

    @Override
    public void apply(Component component) {
        JTextComponent jTextComponent = (JTextComponent)component;
        jTextComponent.paste();
    }
}

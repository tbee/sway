package org.tbee.sway.action;

import javax.swing.Icon;
import javax.swing.text.JTextComponent;
import java.awt.Component;

public class JTextComponentCopy implements Action {

    @Override
    public String label() {
        return "Copy";
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
    public void apply(Component component) {
        JTextComponent jTextComponent = (JTextComponent)component;

        boolean enabled = jTextComponent.isEnabled();
        try {
            jTextComponent.setEnabled(true);
            jTextComponent.selectAll();
            jTextComponent.copy();
        }
        finally {
            jTextComponent.setEnabled(enabled);
        }
    }
}

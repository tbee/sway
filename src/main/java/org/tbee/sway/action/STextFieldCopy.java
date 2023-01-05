package org.tbee.sway.action;

import org.tbee.sway.STextField;

import javax.swing.Icon;
import java.awt.Component;

public class STextFieldCopy implements Action {

    @Override
    public String label() {
        return "Copy value";
    }

    @Override
    public Icon icon() {
        return null;
    }

    @Override
    public boolean isApplicableFor(Component component) {
        return component instanceof STextField;
    }

    @Override
    public void apply(Component component) {
        STextField sTextField = (STextField)component;
        System.out.println("!!!copied " + sTextField.getValue());
    }
}

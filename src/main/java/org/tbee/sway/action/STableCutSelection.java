package org.tbee.sway.action;

import java.awt.Component;
import java.util.Map;

import javax.swing.Icon;

import org.tbee.sway.support.IconRegistry;
import org.tbee.sway.table.STableCore;

public class STableCutSelection implements Action {
    static private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(STableCutSelection.class);

    @Override
    public String label() {
        return "Cut selection";
    }

    @Override
    public Icon icon() {
        return IconRegistry.find(IconRegistry.SwayInternallyUsedIcon.MENU_CUT);
    }

    @Override
    public boolean isApplicableFor(Component component, Map<String, Object> context) {
        return component instanceof STableCore;
    }

    @Override
    public boolean isEnabled(Component component, Map<String, Object> context) {
        STableCore sTable = (STableCore)component;
        return sTable.isEnabled();
    }

    @Override
    public void apply(Component component, String option, Map<String, Object> context) {
        STableCore sTableCore = (STableCore)component;
        sTableCore.getSTable().copy();
    }
}

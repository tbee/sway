package org.tbee.sway.action;

import java.awt.Component;
import java.util.Map;

import javax.swing.Icon;

import org.tbee.sway.support.IconRegistry;
import org.tbee.sway.table.STableCore;

public class STablePasteSelection implements Action {
    static private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(STablePasteSelection.class);

    @Override
    public String label() {
        return "Paste";
    }

    @Override
    public Icon icon() {
        return IconRegistry.find(IconRegistry.SwayInternallyUsedIcon.MENU_PASTE);
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
        sTableCore.getSTable().paste();
    }
}

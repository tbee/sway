package org.tbee.sway.action;

import org.tbee.sway.SIconRegistry;
import org.tbee.sway.table.STableCore;

import javax.swing.Icon;
import java.awt.Component;
import java.util.Map;

public class STablePasteSelection implements Action {
    static private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(STablePasteSelection.class);

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

package org.tbee.sway.action;

import org.tbee.sway.SIconRegistry;
import org.tbee.sway.table.STableCore;

import javax.swing.Icon;
import java.awt.Component;
import java.util.Map;

public class STableCopySelection implements Action {
    static private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(STableCopySelection.class);

    @Override
    public String label() {
        return "Copy selection";
    }

    @Override
    public Icon icon() {
        return SIconRegistry.find(SIconRegistry.SwayInternallyUsedIcon.MENU_COPY);
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

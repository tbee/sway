package org.tbee.sway.action;

import org.tbee.sway.STable;
import org.tbee.sway.SIconRegistry;
import org.tbee.sway.table.STableCore;

import javax.swing.Icon;
import javax.swing.table.JTableHeader;
import java.awt.Component;
import java.util.Map;

public class STableToggleFilter implements Action {

    @Override
    public String label() {
        return "Toggle filter";
    }

    @Override
    public Icon icon() {
        return SIconRegistry.find(SIconRegistry.SwayInternallyUsedIcon.MENU_FILTER);
    }

    @Override
    public boolean isApplicableFor(Component component, Map<String, Object> context) {
        return component instanceof JTableHeader && ((JTableHeader)component).getTable() instanceof STableCore;
    }

    @Override
    public boolean isEnabled(Component component, Map<String, Object> context) {
        JTableHeader jTableHeader = (JTableHeader)component;
        STableCore sTableCore = (STableCore) jTableHeader.getTable();
        STable sTable = sTableCore.getSTable();
        return sTable.isEnabled();
    }

    @Override
    public void apply(Component component, String option, Map<String, Object> context) {
        JTableHeader jTableHeader = (JTableHeader)component;
        STableCore sTableCore = (STableCore) jTableHeader.getTable();
        STable sTable = sTableCore.getSTable();
        sTable.setFilterHeaderEnabled(!sTable.isFilterHeaderEnabled());
    }
}

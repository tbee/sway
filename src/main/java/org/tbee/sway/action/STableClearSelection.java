package org.tbee.sway.action;

import java.awt.Component;
import java.util.Map;

import javax.swing.Icon;

import org.tbee.sway.STable;
import org.tbee.sway.support.IconRegistry;
import org.tbee.sway.table.STableCore;

public class STableClearSelection implements Action {

    @Override
    public String label() {
        return "Clear selection";
    }

    @Override
    public Icon icon() {
        return IconRegistry.find(IconRegistry.SwayInternallyUsedIcon.MENU_SELECTION);
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
    public void apply(Component component, Map<String, Object> context) {
        STableCore sTableCore = (STableCore)component;
        STable sTable = sTableCore.getSTable();
        sTable.clearSelection();
    }
}

package org.tbee.sway.action;

import org.tbee.sway.support.IconRegistry;
import org.tbee.sway.table.STableCore;

import javax.swing.Icon;
import java.awt.Component;
import java.util.Map;

public class STablePasteSelection implements Action {
    static private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(STablePasteSelection.class);

    // These are the same separators as used by Excel
    static final String FIELD_SEPARATOR = "\t";
    static final String RECORD_SEPARATOR = "\n";

    @Override
    public String label() {
        return "Copy";
    }

    @Override
    public Icon icon() {
        return IconRegistry.find("copy", IconRegistry.Usage.MENU);
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
        // TODO
    }
}

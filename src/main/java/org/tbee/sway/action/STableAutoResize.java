package org.tbee.sway.action;

import java.awt.Component;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JTable;

import org.tbee.sway.table.STableCore;

public class STableAutoResize implements Action {
    static private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(STableAutoResize.class);

    @Override
    public String label() {
        return "Auto resize columns";
    }

    @Override
    public Icon icon() {
        return null; //IconRegistry.find(IconRegistry.SwayInternallyUsedIcon.MENU_CUT);
    }

    @Override
    public boolean isApplicableFor(Component component, Map<String, Object> context) {
        return component instanceof STableCore;
    }

    @Override
    public List<String> options() {
    	// TBEERNOT: internationalization
    	return List.of("Off", "All", "Last", "Subsequent", "Next");
    }

    @Override
    public void apply(Component component, String option, Map<String, Object> context) {
        STableCore sTableCore = (STableCore)component;
        switch (option) {
        	case "Off" -> sTableCore.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        	case "All" -> sTableCore.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        	case "Last" -> sTableCore.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        	case "Subsequent" -> sTableCore.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        	case "Next" -> sTableCore.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
        }
    }
}

package org.tbee.sway.action;

import java.awt.Component;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;

public interface Action {
    String label();
    Icon icon();
    boolean isApplicableFor(Component component, Map<String, Object> context);
    void apply(Component component, String option, Map<String, Object> context);

    default boolean isEnabled(Component component, Map<String, Object> context) {
        return true;
    }

    default int order() {
        return 1000;
    }
    
    default List<String> options() {
    	return null;
    }
}

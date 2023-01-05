package org.tbee.sway.action;

import javax.swing.Icon;
import java.awt.Component;
import java.util.Map;

public interface Action {
    String label();
    Icon icon();
    boolean isApplicableFor(Component component, Map<String, Object> context);
    void apply(Component component, Map<String, Object> context);

    default boolean isEnabled(Component component, Map<String, Object> context) {
        return true;
    }

    default int order() {
        return 1000;
    }
}

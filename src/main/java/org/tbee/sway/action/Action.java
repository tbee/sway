package org.tbee.sway.action;

import javax.swing.Icon;
import java.awt.Component;

public interface Action {
    String label();
    Icon icon();
    boolean isApplicableFor(Component component);
    void apply(Component component);

    default boolean isEnabled(Component component) {
        return true;
    }

    default int order() {
        return 1000;
    }
}

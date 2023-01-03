package org.tbee.sway.support;

import javax.swing.JComponent;
import java.awt.Component;
import java.awt.Container;

public class DebugUtil {

    static public String componentTreeAsString(Container c) {
        return componentTreeAsString(c, 0);
    }

    static private String componentTreeAsString(Container container, int indent) {

        // Generate line
        String s = "| ".repeat(indent) + (container.getName() != null ? container.getName() + ": " : "") + container.getClass().getName() + "\n";

        // Walk tree
        for (int i = 0; i < container.getComponentCount(); i++) {
            Component component = container.getComponent(i);
            if (component instanceof JComponent jComponent) {
                s += componentTreeAsString(jComponent, indent + 1);
            }
        }

        // Done
        return s;
    }
}

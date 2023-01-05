package org.tbee.sway.action;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

public class ActionRegistry {

    private static List<Action> actions = new ArrayList<>();

    static {
        // TBEERNOT autodiscovery?
        register(new JLabelCopyText());
        register(new JLabelCopyIcon());
        register(new JTextComponentCopy());
        register(new JTextComponentCut());
        register(new JTextComponentPaste());
        register(new STextFieldCopy());
    }

    /**
     * Register additional actions.
     * @param action
     */
    static public void register(Action action) {
        actions.add(action);
    }
    static public boolean unregister(Action action) {
        return actions.remove(action);
    }

    /**
     * Get the format for a specific component.
     * @param component
     * @return
     */
    static public List<Action> findFor(Component component) {
        return actions.stream() //
                .filter(a -> a.isApplicableFor(component)) //
                .toList();
    }
}

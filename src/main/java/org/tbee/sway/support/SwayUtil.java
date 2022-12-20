package org.tbee.sway.support;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SwayUtil {

    static public Color getErrorColor() {
        try {
            return new Color(UIManager.getColor("Error.color").getRGB());
        }
        catch (NullPointerException e) { // if key is undefined
            return Color.RED; // default value
        }
    }
    static public Color getFirstAlternateRowColor() {
        return new Color(UIManager.getColor("Table.background").getRGB());
    }

    static public Color getSecondAlternateRowColor() {
        return ColorUtil.brighterOrDarker(getFirstAlternateRowColor(), 0.05);
    }

    /**
     * See if this container is still visible to the user (any component in the path toward a window or applet must be visible).
     *
     * @param component
     * @return
     */
    static public boolean isComponentVisibleToUser(Component component) {
        // make sure component is not null to start with
        if (component == null) return false;

        // scan up and determine if any of the component on my way to a window or applet is visible
        do {
            // if this is a window of any kind, stop scanning: if this one finally is also visible, then we are
            if (component instanceof Window) {
                return component.isVisible();
            }

            // if any layer in between is not visible, then the component is not visible
            if (!component.isVisible()) {
                return false;
            }

            // next
            component = component.getParent();
        }
        while (component != null);

        // we have exited the loop, so the last parent is null, which means that the component is not visible
        return false;
    }

    /**
     * Flatten the whole component tree into a list,
     * so it is easily possible to search if some component is present
     *
     * @param c
     * @return
     */
    static public List<Component> flattenComponentTree(JComponent c) {
        List<Component> lList = new ArrayList<Component>();
        flattenComponentTree(c, lList);
        return lList;
    }

    /**
     * And that requires a recursive function.
     *
     * @param c
     * @return
     */
    static private List<Component> flattenComponentTree(JComponent c, List<Component> list) {
        list.add(c);
        for (int i = 0; i < c.getComponentCount(); i++) {
            Component lComponent = c.getComponent(i);
            if (!list.contains(lComponent)) {
                if (lComponent instanceof JComponent) flattenComponentTree((JComponent) lComponent, list);
                else list.add(lComponent); // we don't know how to access the childeren
            }
        }
        return list;
    }
}

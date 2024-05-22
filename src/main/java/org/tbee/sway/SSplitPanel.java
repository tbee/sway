package org.tbee.sway;

import org.tbee.sway.mixin.JComponentMixin;

import javax.swing.JSplitPane;
import java.awt.Component;

/**
 * SSplitPane.of(components...).vertical()
 */
public class SSplitPanel extends JSplitPane implements
        JComponentMixin<SSplitPanel> {

    // ========================================================
    // FLUENT API

    /**
     * The components are placed vertically, with a horizontal splitter
     */
    public SSplitPanel vertical() {
        setOrientation(JSplitPane.VERTICAL_SPLIT);
        return this;
    }

    /**
     * The components are placed horizontally, with a vertical splitter
     */
    public SSplitPanel horizontal() {
        setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        return this;
    }

    /**
     * The first (aka left) component.
     */
    public SSplitPanel first(Component component) {
        setLeftComponent(component);
        return this;
    }

    /**
     * The second (aka right) component.
     */
    public SSplitPanel second(Component component) {
        setRightComponent(component);
        return this;
    }

    public SSplitPanel dividerLocation(double proportionalLocation) {
        setDividerLocation(proportionalLocation);
        return this;
    }


    // ========================================================
    // OF

    static public SSplitPanel of() {
        return new SSplitPanel();
    }

    static public SSplitPanel of(Component first, Component second) {
        return of().first(first).second(second);
    }
}

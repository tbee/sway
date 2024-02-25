package org.tbee.sway;

import org.jdesktop.swingx.StackLayout;
import org.tbee.sway.mixin.ComponentMixin;

import javax.swing.JComponent;
import java.util.Collection;

/**
 * Stacked components
 */
public class SStackedPanel extends SPanelExtendable<SStackedPanel> implements
        ComponentMixin<SStackedPanel> {

    public SStackedPanel() {
        setLayout(new StackLayout());
    }

    static public SStackedPanel of() {
        return new SStackedPanel();
    }

    public static SStackedPanel of(JComponent... components) {
        SStackedPanel panel = of();
        panel.add(components);
        return panel;
    }

    public static SStackedPanel of(Collection<? extends JComponent> components) {
        SStackedPanel panel = of();
        panel.add(components);
        return panel;
    }
}

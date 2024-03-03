package org.tbee.sway;

import org.tbee.sway.mixin.JComponentMixin;

import javax.swing.JComponent;
import java.awt.FlowLayout;
import java.util.Arrays;
import java.util.Collection;

public class SFlowPanel extends SPanelExtendable<SFlowPanel> implements
        JComponentMixin<SFlowPanel> {

    public SFlowPanel() {
        super();
        setLayout(new FlowLayout());
    }

    public SFlowPanel add(JComponent... components) {
        Arrays.stream(components).forEach(c -> super.add(c));
        return this;
    }

    public SFlowPanel add(Collection<? extends JComponent> components) {
        components.forEach(c -> super.add(c));
        return this;
    }

    public static SFlowPanel of() {
        return new SFlowPanel();
    }

    public static SFlowPanel of(JComponent... components) {
        SFlowPanel panel = new SFlowPanel();
        panel.add(components);
        return panel;
    }

    public static SFlowPanel of(Collection<? extends JComponent> components) {
        SFlowPanel panel = new SFlowPanel();
        panel.add(components);
        return panel;
    }
}

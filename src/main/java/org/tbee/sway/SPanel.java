package org.tbee.sway;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.LayoutManager;
import java.util.Arrays;
import java.util.Collection;

public class SPanel extends JPanel {
    public SPanel() {
    }

    public SPanel(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
    }

    public SPanel(LayoutManager layout) {
        super(layout);
    }

    public SPanel(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
    }

    // =========================================================================
    // FLUENT API

    public SPanel addOne(JComponent component) {
        super.add(component);
        return this;
    }

    public SPanel addOne(JComponent component, Object constraints) {
        super.add(component, constraints);
        return this;
    }

    public SPanel addAll(JComponent... component) {
        Arrays.stream(component).forEach(c -> add(c));
        return this;
    }

    public SPanel addAll(Collection<? extends JComponent> components) {
        components.forEach(c -> add(c));
        return this;
    }
}

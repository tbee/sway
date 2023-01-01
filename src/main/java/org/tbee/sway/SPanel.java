package org.tbee.sway;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.LayoutManager;
import java.util.Arrays;
import java.util.Collection;

public class SPanel extends JPanel {
    public SPanel() {
    }

    public SPanel(LayoutManager layout) {
        super(layout);
    }

    // =========================================================================
    // FLUENT API

    public SPanel add(JComponent component) {
        super.add(component);
        return this;
    }

    public SPanel add(JComponent component, Object constraints) {
        super.add(component, constraints);
        return this;
    }

    public SPanel add(JComponent... component) {
        Arrays.stream(component).forEach(c -> add(c));
        return this;
    }

    public SPanel add(Collection<? extends JComponent> components) {
        components.forEach(c -> add(c));
        return this;
    }

    public SPanel doubleBuffered(boolean v) {
        super.setDoubleBuffered(v);
        return this;
    }
}

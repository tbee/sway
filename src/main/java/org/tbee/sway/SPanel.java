package org.tbee.sway;

import java.awt.LayoutManager;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class SPanel extends JPanel {
    public SPanel() {
    }

    public SPanel(LayoutManager layout) {
        super(layout);
    }

    // =========================================================================
    // FLUENT API

    public SPanel name(String v) {
        setName(v);
        return this;
    }
    
    public SPanel margin(int top, int left, int bottom, int right) {
    	setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
        return this;
    }
    public SPanel margin(int v) {
        return margin(v, v, v, v);
    }

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

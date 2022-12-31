package org.tbee.sway;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import java.util.Arrays;
import java.util.Collection;

public class SFlowPanel extends JPanel {

    public SFlowPanel() {
        super();
        setLayout(new FlowLayout());
    }
    
    public SFlowPanel(JComponent... components) {
        this();
        add(components);
    }

    public SFlowPanel(Collection<? extends JComponent> components) {
        this();
        add(components);
    }

    public SFlowPanel add(JComponent... components) {
        Arrays.stream(components).forEach(c -> super.add(c));
        return this;
    }

    public SFlowPanel add(Collection<? extends JComponent> components) {
        components.forEach(c -> super.add(c));
        return this;
    }
}

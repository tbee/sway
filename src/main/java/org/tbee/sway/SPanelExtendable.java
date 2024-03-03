package org.tbee.sway;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.LayoutManager;
import java.util.Arrays;
import java.util.Collection;

abstract public class SPanelExtendable<T extends Component> extends JPanel {
    public SPanelExtendable() {
    }

    public SPanelExtendable(LayoutManager layout) {
        super(layout);
    }

    // =========================================================================
    // FLUENT API

    public T add(JComponent component) {
        super.add(component);
        return (T)this;
    }

    public T add(JComponent component, Object constraints) {
        super.add(component, constraints);
        return (T)this;
    }

    public T add(JComponent... component) {
        Arrays.stream(component).forEach(c -> add(c));
        return (T)this;
    }

    public T add(Collection<? extends JComponent> components) {
        components.forEach(c -> add(c));
        return (T)this;
    }
}

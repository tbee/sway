package org.tbee.sway;

import org.tbee.sway.mixin.PropertyChangeListenerMixin;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.LayoutManager;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Collection;

abstract public class SPanelExtendable<T extends Component> extends JPanel implements PropertyChangeListenerMixin<T> {
    public SPanelExtendable() {
    }

    public SPanelExtendable(LayoutManager layout) {
        super(layout);
    }

    // =========================================================================
    // FLUENT API

    public T name(String v) {
        setName(v);
        return (T)this;
    }
    
    /**
     * Short for creating an empty border
     * @param top
     * @param left
     * @param bottom
     * @param right
     * @return
     */
    public T margin(int top, int left, int bottom, int right) {
    	setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
        return (T)this;
    }
    public T margin(int v) {
        return margin(v, v, v, v);
    }

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

    public T doubleBuffered(boolean v) {
        super.setDoubleBuffered(v);
        return (T)this;
    }

    public SPanelExtendable<T> withPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        super.addPropertyChangeListener(propertyName, listener);
        return this;
    }
    public SPanelExtendable<T> withPropertyChangeListener(PropertyChangeListener listener) {
        super.addPropertyChangeListener(listener);
        return this;
    }

    public T overlayWith(Component overlayComponent) {
        SOverlayPane.overlayWith(this, overlayComponent);
        return (T)this;
    }
    public T removeOverlay(Component overlayComponent) {
        SOverlayPane.removeOverlay(this, overlayComponent);
        return (T)this;
    }
}

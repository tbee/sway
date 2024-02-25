package org.tbee.sway.mixin;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;

public interface ComponentMixin<T extends Component> extends
        PropertyChangeListenerMixin<T>,
        KeyListenerMixin<T>,
        OverlayMixin<T>,
        FontMixin<T> {

    void setName(String name);

    default T name(String v) {
        setName(v);
        return (T) this;
    }

    void setEnabled(boolean b);

    default T enabled(boolean v) {
        setEnabled(v);
        return (T) this;
    }

    void setVisible(boolean b);

    default T visible(boolean v) {
        setVisible(v);
        return (T) this;
    }

    void setForeground(Color c);

    default T foreground(Color c) {
        setForeground(c);
        return (T) this;
    }

    void setBackground(Color c);

    default T background(Color c) {
        setBackground(c);
        return (T) this;
    }

    void setLocation(int x, int y);

    default T location(int x, int y) {
        setLocation(x, y);
        return (T) this;
    }

    void setLocation(Point p);
    default T location(Point c) {
        setLocation(c);
        return (T) this;
    }

    void setSize(int w, int h);

    default T size(int w, int h) {
        setSize(w, h);
        return (T) this;
    }

    void setSize(Dimension p);
    default T size(Dimension c) {
        setSize(c);
        return (T) this;
    }
}
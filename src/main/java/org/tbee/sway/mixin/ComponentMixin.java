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


    default T name(String v) {
        ((T)this).setName(v);
        return (T) this;
    }


    default T enabled(boolean v) {
        ((T)this).setEnabled(v);
        return (T) this;
    }


    default T visible(boolean v) {
        ((T)this).setVisible(v);
        return (T) this;
    }


    default T foreground(Color c) {
        ((T)this).setForeground(c);
        return (T) this;
    }


    default T background(Color c) {
        ((T)this).setBackground(c);
        return (T) this;
    }


    default T location(int x, int y) {
        ((T)this).setLocation(x, y);
        return (T) this;
    }

    default T location(Point c) {
        ((T)this).setLocation(c);
        return (T) this;
    }


    default T size(int w, int h) {
        ((T)this).setSize(w, h);
        return (T) this;
    }

    default T size(Dimension c) {
        ((T)this).setSize(c);
        return (T) this;
    }
}
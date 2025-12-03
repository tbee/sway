package org.tbee.sway.mixin;

import org.tbee.sway.binding.BindingEndpoint;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;

public interface ComponentMixin<T extends Component> extends
        PropertyChangeListenerMixin<T>,
        KeyListenerMixin<T>,
        FocusListenerMixIn<T>,
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
    String ENABLED = "enabled";
    default BindingEndpoint<Boolean> enabled$() {
        return BindingEndpoint.of(this, ENABLED);
    }


    default T visible(boolean v) {
        ((T)this).setVisible(v);
        return (T) this;
    }
    String VISIBLE = "visible";
    default BindingEndpoint<Boolean> visible$() {
        return BindingEndpoint.of(this, VISIBLE);
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

    default T minSizeIsPrefSize() {
        ((T)this).setMinimumSize(((T)this).getPreferredSize());
        return (T) this;
    }

    ///  Set height, using the current width
    default T height(int v) {
        Dimension size = ((T) this).getSize();
        ((T)this).setSize(size.width, v);
        return (T) this;
    }

    ///  Set width, using the current height
    default T width(int v) {
        Dimension size = ((T) this).getSize();
        ((T)this).setSize(v, size.height);
        return (T) this;
    }
}
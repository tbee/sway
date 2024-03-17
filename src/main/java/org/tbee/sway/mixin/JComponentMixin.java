package org.tbee.sway.mixin;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.Border;

public interface JComponentMixin<T extends JComponent> extends
        ComponentMixin<T>,
        PropertyChangeListenerMixin<T>,
        KeyListenerMixin<T>,
        OverlayMixin<T>,
        FontMixin<T> {


    default T border(Border border) {
        ((T)this).setBorder(border);
        return (T) this;
    }

    /**
     * Short for creating an empty border
     * @param top
     * @param left
     * @param bottom
     * @param right
     * @return
     */
    default T margin(int top, int left, int bottom, int right) {
        border(BorderFactory.createEmptyBorder(top, left, bottom, right)); // empty border is transparent
        return (T)this;
    }
    default T margin(int v) {
        return margin(v, v, v, v);
    }

    default T doubleBuffered(boolean v) {
        ((T)this).setDoubleBuffered(v);
        return (T)this;
    }

    default T opaque(boolean v) {
        ((T)this).setOpaque(v);
        return (T)this;
    }
}
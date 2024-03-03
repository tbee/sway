package org.tbee.sway.mixin;

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
}
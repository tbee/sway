package org.tbee.sway.mixin;

import org.tbee.sway.SOverlayPane;

import java.awt.Component;

public interface OverlayMixin<T extends Component> {

    default T overlayWith(Component overlayComponent) {
        SOverlayPane.overlayWith((T)this, overlayComponent);
        return (T)this;
    }
    default T removeOverlay(Component overlayComponent) {
        SOverlayPane.removeOverlay((T)this, overlayComponent);
        return (T)this;
    }
}

package org.tbee.sway.mixin;

import java.awt.Window;

public interface WindowMixin<T extends Window> {

    /**
     * When the window is moved to a different screen
     * @param runnable
     * @return
     */
    default T onScreenChange(Runnable runnable) {
        ((T)this).addPropertyChangeListener("graphicsConfiguration", evt -> runnable.run());
        return (T)this;
    }
}

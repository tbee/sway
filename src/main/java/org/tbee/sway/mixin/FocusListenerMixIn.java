package org.tbee.sway.mixin;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.function.Consumer;

public interface FocusListenerMixIn<T extends Component> {

    void addFocusListener(FocusListener l);

    default T onFocusGained(Consumer<FocusEvent> consumer) {
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                consumer.accept(e);
            }

            @Override
            public void focusLost(FocusEvent e) {
            }
        });
        return (T)this;
    }

    default T onFocusLost(Consumer<FocusEvent> consumer) {
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                consumer.accept(e);
            }
        });
        return (T)this;
    }
}

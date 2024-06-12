package org.tbee.sway.mixin;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.function.Consumer;

public interface FocusListenerMixIn<T extends Component> extends ExceptionHandleMixin<T> {

    void addFocusListener(FocusListener l);

    default T onFocusGained(Consumer<FocusEvent> consumer) {
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                try {
                    consumer.accept(e);
                }
                catch (Exception ex) {
                    handleException(ex);
                }
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
                try {
                    consumer.accept(e);
                }
                catch (Exception ex) {
                    handleException(ex);
                }
            }
        });
        return (T)this;
    }
}

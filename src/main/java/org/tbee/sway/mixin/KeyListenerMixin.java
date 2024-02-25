package org.tbee.sway.mixin;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.function.Consumer;

public interface KeyListenerMixin<T extends Component> {

    void addKeyListener(KeyListener l);

    default T onKeyTyped(Consumer<KeyEvent> consumer) {
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                consumer.accept(e);
            }

            @Override
            public void keyPressed(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {}
        });
        return (T)this;
    }

    default T onKeyPressed(Consumer<KeyEvent> consumer) {
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) { }

            @Override
            public void keyPressed(KeyEvent e) {
                consumer.accept(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {}
        });
        return (T)this;
    }

    default T onKeyReleased(Consumer<KeyEvent> consumer) {
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) { }

            @Override
            public void keyPressed(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {
                consumer.accept(e);
            }
        });
        return (T)this;
    }
}

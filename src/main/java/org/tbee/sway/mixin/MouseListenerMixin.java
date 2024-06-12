package org.tbee.sway.mixin;

import javax.swing.JComponent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.function.Consumer;

public interface MouseListenerMixin<T extends JComponent> extends ExceptionHandleMixin<T> {

    void addMouseListener(MouseListener l);

    default T onClick(Consumer<MouseEvent> consumer) {
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    try {
                        consumer.accept(e);
                    }
                    catch (Exception ex) {
                        handleException(ex);
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        });
        return (T)this;
    }

    default T onDoubleClick(Consumer<MouseEvent> consumer) {
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    try {
                        consumer.accept(e);
                    }
                    catch (Exception ex) {
                        handleException(ex);
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        });
        return (T)this;
    }

    default T onMousePress(Consumer<MouseEvent> consumer) {
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {}

            @Override
            public void mousePressed(MouseEvent e) {
                try {
                    consumer.accept(e);
                }
                catch (Exception ex) {
                    handleException(ex);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        });
        return (T)this;
    }

    default T onMouseRelease(Consumer<MouseEvent> consumer) {
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {}

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {
                try {
                    consumer.accept(e);
                }
                catch (Exception ex) {
                    handleException(ex);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        });
        return (T)this;
    }

    default T onMouseEnter(Consumer<MouseEvent> consumer) {
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {}

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {
                try {
                    consumer.accept(e);
                }
                catch (Exception ex) {
                    handleException(ex);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {}
        });
        return (T)this;
    }

    default T onMouseExit(Consumer<MouseEvent> consumer) {
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {}

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {
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

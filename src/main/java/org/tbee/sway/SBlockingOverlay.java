package org.tbee.sway;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;

/**
 * Placing a blocking overlay over a component will block mouse clicks.
 */
public class SBlockingOverlay extends JPanel implements SOverlayPane.OnOverlayCallback, SOverlayPane.OnRemoveCallback {

    final private Component component;

    /**
     * Use this constructor is the overlay completely convers the Frame/Dialog.
     * Otherwise the user might move the keyboard focus under the overlay by pressing TAB from an unoverlaid field.
     */
    public SBlockingOverlay() {
        this(null);
    }

    /**
     * Use this constructor if the overlay does cover the whole Frame/Dialog,
     * and you want to prevent the keyboard focus moving under the overlay by pressing TAB from an unoverlaid field.
     *
     * @param component the component being overlaid
     */
    public SBlockingOverlay(Component component) {
        this.component = component;

        setOpaque(true);
        setBackground(new Color(150, 150, 150, 123));

        addKeyListener(new KeyAdapter() {});
        addMouseListener(new MouseAdapter() {});

    }

    @Override
    public void onOverlay() {
        setFocusable(true);
        setRequestFocusEnabled(true);
        requestFocus();
        requestFocusInWindow();
        setFocusTraversalKeysEnabled(false);

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("focusOwner", evt -> System.out.println("propertyChange " + evt));

//
//        ((JPanel)component).getComponent(0).addFocusListener(new FocusAdapter() {
//            @Override
//            public void focusGained(FocusEvent e) {
//                SBlockingOverlay.this.requestFocus();
//            }
//        });
    }

    @Override
    public void onRemove() {

    }
}

package org.tbee.sway;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.beans.PropertyChangeListener;

/**
 * Placing a blocking overlay over a component will block mouse clicks.
 */
public class SBlockingOverlay extends JPanel implements SOverlayPane.OverlaidComponentCallback, SOverlayPane.OnOverlayCallback, SOverlayPane.OnRemoveCallback {

    final static private String FOCUS_OWNER = "focusOwner";

    private Component component = null;

    /**
     *
     */
    public SBlockingOverlay() {

        setOpaque(true);
        setBackground(new Color(150, 150, 150, 123));
        setFocusable(true);
        setRequestFocusEnabled(true);
        setFocusTraversalKeysEnabled(false);

        addKeyListener(new KeyAdapter() {});
        addMouseListener(new MouseAdapter() {});
    }


    @Override
    public void setComponent(Component component) {
        this.component = component;
    }

    @Override
    public void onOverlay() {
        claimFocus();
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener(FOCUS_OWNER, focusOwnerListener);
    }

    @Override
    public void onRemove() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removePropertyChangeListener(FOCUS_OWNER, focusOwnerListener);
    }

    private void claimFocus() {
        requestFocus();
        requestFocusInWindow();
    }

    // Listen for if a component has gotten focus that is (a child of) the overlaid component.
    // If that is so, move the focus back to the overlay.
    PropertyChangeListener focusOwnerListener = evt -> {
        Object newValue = evt.getNewValue();
        if (SBlockingOverlay.this.component != null && newValue != null && newValue instanceof Component focussedComponent) {
            while (focussedComponent != null) {
                if (focussedComponent == SBlockingOverlay.this.component) {
                    claimFocus();
                }
                focussedComponent = focussedComponent.getParent();
            }
        }
    };
}

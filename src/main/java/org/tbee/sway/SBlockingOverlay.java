package org.tbee.sway;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;

/**
 * Placing a blocking overlay over a component will block mouse clicks.
 */
public class SBlockingOverlay extends JPanel {

    /**
     * @param component the component being overlaid
     */
    public SBlockingOverlay(Component component) {
        setOpaque(true);
        setBackground(new Color(150, 150, 150, 123));

        addKeyListener(new KeyAdapter() {});
        addMouseListener(new MouseAdapter() {});

        setFocusable(true);
        setRequestFocusEnabled(true);
        requestFocus();
//
//        ((JPanel)component).getComponent(0).addFocusListener(new FocusAdapter() {
//            @Override
//            public void focusGained(FocusEvent e) {
//                SBlockingOverlay.this.requestFocus();
//            }
//        });
    }
}

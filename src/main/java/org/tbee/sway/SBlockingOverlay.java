package org.tbee.sway;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.event.MouseAdapter;

public class SBlockingOverlay extends JPanel {

    public SBlockingOverlay() {
        setFocusable(false);
        addMouseListener(new MouseAdapter() {});
        setOpaque(true);
        setBackground(new Color(150, 150, 150, 123));
    }
}

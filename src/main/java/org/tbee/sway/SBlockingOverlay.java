package org.tbee.sway;

import org.tbee.sway.support.HAlign;

import javax.swing.Icon;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;

public class SBlockingOverlay extends JPanel {

    public SBlockingOverlay() {
        this((Component)null);
    }

    public SBlockingOverlay(Icon icon) {
        this(SLabel.of(icon).hAlign(HAlign.CENTER));
    }

    public SBlockingOverlay(Component component) {
        setFocusable(false);
        addMouseListener(new MouseAdapter() {});
        setOpaque(true);
        setBackground(new Color(150, 150, 150, 123));

        setLayout(new BorderLayout());
        add(component, BorderLayout.CENTER);
    }
}

package org.tbee.sway;

import org.tbee.sway.support.HAlign;
import org.tbee.sway.support.IconRegistry;

import javax.swing.Icon;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;

public class SBlockingOverlay extends JPanel {

    private final SLabel label = SLabel.of();

    public SBlockingOverlay() {
        this(null, IconRegistry.find(IconRegistry.SwayInternallyUsedIcon.OVERLAY_LOADING));
    }

    public SBlockingOverlay(String text, Icon icon) {
        setFocusable(false);
        addMouseListener(new MouseAdapter() {});
        setOpaque(true);
        setBackground(new Color(150, 150, 150, 123));

        setLayout(new BorderLayout());
        add(label, BorderLayout.CENTER);

        label.text(text)
             .icon(icon)
             .hAlign(HAlign.CENTER);
    }
}

package org.tbee.sway;

import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class TestUtil {

    static public JFrame inJFrame(JComponent... components) {
        var jPanel = new JPanel();
        jPanel.setLayout(new MigLayout());
        for (JComponent component : components) {
            jPanel.add(component, new CC());
        }
        return inJFrame(jPanel);
    }

    static public JFrame inJFrame(JComponent component) {
        JFrame jFrame = new JFrame();
        jFrame.setContentPane(component);
        jFrame.pack();
        return jFrame;
    }

    static public void sleep(int ms) {
        try {
            Thread.sleep(ms);
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

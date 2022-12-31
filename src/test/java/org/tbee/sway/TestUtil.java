package org.tbee.sway;

import javax.swing.JComponent;
import javax.swing.JFrame;
import java.util.Collection;

public class TestUtil {

    static public JFrame inJFrame(Collection<? extends JComponent> components) {
        var sPanel = new SMigLayoutPanel(components);
        return inJFrame(sPanel);
    }

    static public JFrame inJFrame(JComponent... components) {
        var sPanel = new SMigLayoutPanel(components);
        return inJFrame(sPanel);
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

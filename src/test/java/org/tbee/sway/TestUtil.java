package org.tbee.sway;

import javax.swing.JComponent;
import javax.swing.JFrame;
import java.util.Collection;

public class TestUtil {

    static public JFrame inSFrame(Collection<? extends JComponent> components) {
        var sPanel = SMigPanel.of(components);
        return inSFrame(sPanel);
    }

    static public JFrame inSFrame(JComponent... components) {
        var sPanel = SMigPanel.of(components);
        return inSFrame(sPanel);
    }

    static public SFrame inSFrame(JComponent component) {
        SFrame sFrame = new SFrame();
        sFrame.setContentPane(component);
        sFrame.pack();
        return sFrame;
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

package org.tbee.sway;

import net.miginfocom.swing.MigLayout;

import javax.swing.JComponent;
import javax.swing.JFrame;
import java.util.Collection;

public class TestUtil {

    static public JFrame inJFrame(Collection<JComponent> components) {
        var sPanel = new SPanel(new MigLayout()) //
                .addAll(components);
        return inJFrame(sPanel);
    }

    static public JFrame inJFrame(JComponent... components) {
        var sPanel = new SPanel(new MigLayout()) //
                .addAll(components);
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

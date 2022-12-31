package org.tbee.sway;

import net.miginfocom.layout.CC;
import org.tbee.sway.support.MigLayoutUtil;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.util.Arrays;
import java.util.Collection;

// TODO
// - strongly typed API calls for all MigLayout stuff

public class SMigLayoutPanel extends JPanel {

    public SMigLayoutPanel() {
        super();
        setLayout(MigLayoutUtil.newMigLayout());
    }

    public SMigLayoutPanel(JComponent... components) {
        this();
        add(components);
    }

    public SMigLayoutPanel(Collection<? extends JComponent> components) {
        this();
        add(components);
    }

    public SMigLayoutPanel add(JComponent... components) {
        Arrays.stream(components).forEach(c -> super.add(c));
        return this;
    }

    public SMigLayoutPanel add(Collection<? extends JComponent> components) {
        components.forEach(c -> super.add(c));
        return this;
    }

    public SMigLayoutPanel add(JComponent component, CC cc) {
        super.add(component, cc);
        return this;
    }
}

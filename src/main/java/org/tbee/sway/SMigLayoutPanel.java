package org.tbee.sway;

import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.util.Arrays;
import java.util.Collection;

// TODO
// - strongly typed API calls for all MigLayout stuff

public class SMigLayoutPanel extends JPanel {

    final private LC lc = new LC();
    final private AC rowAC = new AC();
    final private AC colAC = new AC();
    final private MigLayout migLayout = new MigLayout(lc, colAC, rowAC);

    public LC getLC() {
        return lc;
    }

    public SMigLayoutPanel() {
        super();
        setLayout(migLayout);
        lc.hideMode(2); // invisible components are 0x0*
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

    // ===================================================================
    // FLUENT API

    public SMigLayoutPanel fill() {
        lc.fill();
        return this;
    }

    public SMigLayoutPanel noMargins() {
        lc.insets("0", "0", "0", "0");
        return this;
    }

    public SMigLayoutPanel noGaps() {
        lc.gridGap("0", "0");
        return this;
    }
}

package org.tbee.sway;

import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;
import org.tbee.sway.mixin.JComponentMixin;

import javax.swing.JComponent;
import java.awt.Component;
import java.util.Collection;

public class SHPanel extends SPanelExtendable<SHPanel> implements
        JComponentMixin<SHPanel> {

    // Need to declare these specifically, because the getters return String
    final private LC lc = new LC();
    final private AC rowAC = new AC();
    final private AC colAC = new AC();
    final private MigLayout migLayout = new MigLayout(lc, colAC, rowAC);

    public SHPanel() {
        setLayout(migLayout);
        //lc.debug();
    }

    protected void addImpl(Component comp, Object constraints, int index) {
        super.addImpl(comp, new CC(), index);
    }

    static public SHPanel of() {
        return new SHPanel();
    }

    public static SHPanel of(JComponent... components) {
        SHPanel panel = of();
        panel.add(components);
        return panel;
    }

    public static SHPanel of(Collection<? extends JComponent> components) {
        SHPanel panel = of();
        panel.add(components);
        return panel;
    }

    public enum Align {TOP, CENTER, BOTTOM, BASELINE}

    public SHPanel align(Align v) {
        for (Component component : getComponents()) {
            CC cc = (CC)migLayout.getComponentConstraints(component);
            cc.alignY(v.toString().toLowerCase());
        }
        return this;
    }

    public SHPanel gap(int v) {
        lc.gridGap("" + v, "0");
        migLayout.setLayoutConstraints(lc); // reapply
        return this;
    }

    @Override
    public SHPanel margin(int top, int left, int bottom, int right) {
        lc.insets(top + "px", left + "px", bottom + "px", right + "px");
        migLayout.setLayoutConstraints(lc); // reapply
        return this;
    }
}

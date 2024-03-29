package org.tbee.sway;

import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;
import org.tbee.sway.mixin.JComponentMixin;

import javax.swing.JComponent;
import java.awt.Component;
import java.util.Collection;

/**
 * Vertical layout panel
 */
public class SVPanel extends SPanelExtendable<SVPanel> implements
        JComponentMixin<SVPanel> {

    // Need to declare these specifically, because the getters return String
    final private LC lc = new LC();
    final private AC rowAC = new AC();
    final private AC colAC = new AC();
    final private MigLayout migLayout = new MigLayout(lc, colAC, rowAC);

    public SVPanel() {
        setLayout(migLayout);
        //lc.debug();
    }

    protected void addImpl(Component comp, Object constraints, int index) {
        super.addImpl(comp, new CC().wrap(), index);
    }

    static public SVPanel of() {
        return new SVPanel();
    }

    public static SVPanel of(JComponent... components) {
        SVPanel panel = of();
        panel.add(components);
        return panel;
    }

    public static SVPanel of(Collection<? extends JComponent> components) {
        SVPanel panel = of();
        panel.add(components);
        return panel;
    }

    public SVPanel gap(int v) {
        lc.gridGap("0", "" + v);
        migLayout.setLayoutConstraints(lc); // reapply
        return this;
    }

    public enum Align {LEADING, LEFT, CENTER, RIGHT, TRAILING}

    public SVPanel align(Align v) {
        for (Component component : getComponents()) {
            CC cc = (CC)migLayout.getComponentConstraints(component);
            cc.alignX(v.toString().toLowerCase());
        }
        return this;
    }

    public SVPanel fillWidth(boolean v) {
        for (Component component : getComponents()) {
            CC cc = (CC)migLayout.getComponentConstraints(component);
            cc.grow(v ? 100 : 0);
        }
        return this;
    }

    @Override
    public SVPanel margin(int top, int left, int bottom, int right) {
        lc.insets(top + "px", left + "px", bottom + "px", right + "px");
        migLayout.setLayoutConstraints(lc); // reapply
        return this;
    }
}

package org.tbee.sway;

import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;
import org.tbee.sway.mixin.JComponentMixin;

import javax.swing.JComponent;
import java.awt.Component;
import java.util.Collection;
import java.util.function.Consumer;

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
    }

    public SVPanel debug() {
        lc.debug();
        return this;
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

    @Override
    public SVPanel margin(int top, int left, int bottom, int right) {
        lc.insets(top + "px", left + "px", bottom + "px", right + "px");
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
            fillWidth(component, v);
        }
        return this;
    }
    public SVPanel fillWidth(Component component, boolean v) {
        CC cc = (CC)migLayout.getComponentConstraints(component);
        cc.growX(v ? 100 : 0);
        cc.pushX(v ? 100f : 0f);
        return this;
    }
    public SVPanel fillWidth(int componentIndex, boolean v) {
        return fillWidth(getComponent(componentIndex), v);
    }

    public SVPanel fillHeight(boolean v) {
        for (Component component : getComponents()) {
            fillHeight(component, v);
        }
        return this;
    }
    public SVPanel fillHeight(Component component, boolean v) {
        CC cc = (CC)migLayout.getComponentConstraints(component);
        cc.growY(v ? 100 : 0);
        cc.pushY(v ? 100f : 0f);
        return this;
    }
    public SVPanel fillHeight(int componentIndex, boolean v) {
        return fillHeight(getComponent(componentIndex), v);
    }

    // Just changing the CC values does not effectuate them, setComponentConstraints needs to be called.
    private CC cc(Component component) {
        CC cc = (CC)migLayout.getComponentConstraints(component);
        if (cc == null) {
            cc = new CC();
        }
        return cc;
    }
    private void cc(Component component, CC cc) {
        migLayout.setComponentConstraints(component, cc);
    }
    public SVPanel cc(Component component, Consumer<CC> consumer) {
        CC cc = cc(component);
        consumer.accept(cc);
        cc(component, cc);
        return this;
    }
}

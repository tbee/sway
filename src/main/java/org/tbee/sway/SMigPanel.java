package org.tbee.sway;

import net.miginfocom.layout.AC;
import net.miginfocom.layout.AlignX;
import net.miginfocom.layout.AlignY;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.HideMode;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;
import org.tbee.sway.mixin.JComponentMixin;

import javax.swing.JComponent;
import java.awt.Component;
import java.util.Collection;

public class SMigPanel extends SPanelExtendable<SMigPanel> implements
        JComponentMixin<SMigPanel> {

    // Need to declare these specifically, because the getters return String
    final private LC lc = new LC();
    final private AC rowAC = new AC();
    final private AC colAC = new AC();
    final private MigLayout migLayout = new MigLayout(lc, colAC, rowAC);

    public SMigPanel() {
        super();
        setLayout(migLayout);
        hideMode(HideMode.SIZE_0_GAPS_0);
    }

    public SMigPanel(JComponent... components) {
        this();
        add(components);
    }

    public SMigPanel(Collection<? extends JComponent> components) {
        this();
        add(components);
    }

    // =========================================================================
    // FLUENT API for adding components

    public SMigPanel add(JComponent component, CC cc) {
        super.add(component, cc);
        return this;
    }

    public CC addLabel(JComponent component) {
        CC cc = new CC() //
                .alignX(AlignX.TRAILING) //
                .alignY(AlignY.BASELINE);
        add(component, cc);
        return cc;
    }

    public CC addField(JComponent component) {
        CC cc = new CC() //
                .alignX(AlignX.LEADING) //
                .alignY(AlignY.TOP);
        add(component, cc);
        return cc;
    }

    /**
     * Add both a label and field side by side
     * @param labelComponent
     * @param fieldComponent
     * @return CC of field component
     */
    public CC addLabelAndField(JComponent labelComponent, JComponent fieldComponent) {
    	CC labelCC = addLabel(labelComponent);
        // Automatically switch label to alignY top if the field is larger than roughly 1 line
        if (fieldComponent.getPreferredSize().height > (1.1 * STextField.ofString().value("X").getPreferredSize().height)) { // if (fieldComponent instanceof STextArea)        
        	labelCC.alignY(AlignY.TOP);
        }
        return addField(fieldComponent);
    }

    /**
     * Add both a label and field side by side
     * @param label
     * @param fieldComponent
     * @return CC of field component
     */
    public CC addLabelAndField(String label, JComponent fieldComponent) {
    	return addLabelAndField(new SLabel(label), fieldComponent);
    }

    public CC getCCFor(Component component) {
        CC cc = (CC)migLayout.getComponentConstraints(component);
        if (cc == null) {
            cc = new CC();
        }
        return cc;
    }
    public void setCCFor(Component component, CC cc) {
        migLayout.setComponentConstraints(component, cc);
    }

    // =========================================================================
    // FLUENT API panel level

    static public SMigPanel of() {
        return new SMigPanel();
    }

    static public SMigPanel of(JComponent... components) {
        return of().add(components);
    }

    static public SMigPanel of(Collection<? extends JComponent> components) {
        return of().add(components);
    }

    /**
     * Toggle debug
     * @return
     */
    public SMigPanel debug() {
        lc.debug(lc.getDebugMillis() > 0 ? 0 : 1000); // toggle debug
        return this;
    }

    public SMigPanel fill() {
        lc.fill();
        migLayout.setLayoutConstraints(lc); // reapply
        return this;
    }

    public SMigPanel noMargins() {
        lc.insets("0", "0", "0", "0");
        migLayout.setLayoutConstraints(lc); // reapply
        return this;
    }

    public SMigPanel noGaps() {
        lc.gridGap("0", "0");
        migLayout.setLayoutConstraints(lc); // reapply
        return this;
    }
    
    public SMigPanel alignX(String v) {
        lc.alignX(v);
        migLayout.setLayoutConstraints(lc); // reapply
        return this;
    }

    public SMigPanel alignX(AlignX v) {
        lc.alignX(v);
        migLayout.setLayoutConstraints(lc); // reapply
        return this;
    }

    public SMigPanel alignY(String v) {
        lc.alignY(v);
        migLayout.setLayoutConstraints(lc); // reapply
        return this;
    }
    public SMigPanel alignY(AlignY v) {
        lc.alignY(v);
        migLayout.setLayoutConstraints(lc); // reapply
        return this;
    }

    /**
     * Wrap after the last component
     */
    public SMigPanel wrap() {
        Component component = getComponent(getComponentCount() - 1);
        CC cc = getCCFor(component);
        setCCFor(component, cc.wrap()); // reapply
        return this;
    }

    /**
     * NORMAL: Bounds will be calculated as if the component was visible.<br>
     * SIZE_0_RETAIN_GAPS: If hidden the size will be 0, 0 but the gaps remain.<br>
     * SIZE_0_GAPS_0: If hidden the size will be 0, 0 and gaps set to zero.<br>
     * DISREGARD: If hidden the component will be disregarded completely and not take up a cell in the grid..
     */
    public SMigPanel hideMode(HideMode v) {
        lc.hideMode(v);
        migLayout.setLayoutConstraints(lc); // reapply
        return this;
    }

    @Override
    public SMigPanel margin(int top, int left, int bottom, int right) {
        lc.insets(top + "px", left + "px", bottom + "px", right + "px");
        migLayout.setLayoutConstraints(lc); // reapply
        return this;
    }

    // ---
    // SBorderPanel like
    
    public SMigPanel center(JComponent component) {
        super.add(component, "dock center");
        return this;
    }

    public SMigPanel north(JComponent component) {
        super.add(component, new CC().dockNorth());
        return this;
    }

    public SMigPanel east(JComponent component) {
        super.add(component, new CC().dockEast());
        return this;
    }

    public SMigPanel south(JComponent component) {
        super.add(component, new CC().dockSouth());
        return this;
    }

    public SMigPanel west(JComponent component) {
        super.add(component, new CC().dockWest());
        return this;
    }
}

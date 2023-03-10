package org.tbee.sway;

import java.awt.Component;
import java.util.Collection;

import javax.swing.JComponent;

import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

// TODO
// - strongly typed API calls for all MigLayout stuff

public class SMigPanel extends SPanelExtendable<SMigPanel> {
	
	static public enum AlignX {LEADING, LEFT, RIGHT, TRAILING}
	static public enum AlignY {TOP, BOTTOM, BASELINE}


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
            .alignX("trailing") //
            .alignY("baseline");
        add(component, cc);
        return cc;
    }

    public CC addField(JComponent component) {
        CC cc = new CC() //
                .alignX("leading") //
                .alignY("top");
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
        if (fieldComponent.getPreferredSize().height > (1.1 * STextField.ofString().value("X").getPreferredSize().height)) { // if (fieldComponent instanceof STextArea)        
        	labelCC.alignY("top");
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
    public SMigPanel alignX(AlignX alignX) {
        return alignX(alignX == null ? null : alignX.toString().toLowerCase());
    }

    public SMigPanel alignY(String v) {
        lc.alignY(v);
        migLayout.setLayoutConstraints(lc); // reapply
        return this;
    }
    public SMigPanel alignY(AlignY alignY) {
        return alignX(alignY == null ? null : alignY.toString().toLowerCase());
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
    public SMigPanel hideMode(HideMode v)
    {
        lc.setHideMode(v.code);
        migLayout.setLayoutConstraints(lc); // reapply
        return this;
    }
    enum HideMode {
        NORMAL(0), SIZE_0_RETAIN_GAPS(1), SIZE_0_GAPS_0(2), DISREGARD(3);

        private final int code;

        private HideMode(int code) {
            this.code = code;
        }

        static public HideMode of(int code) {
            for (HideMode hideMode : values()) {
                if (hideMode.code == code) {
                    return hideMode;
                }
            }
            throw new IllegalArgumentException("Code does not exist " + code);
        }
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

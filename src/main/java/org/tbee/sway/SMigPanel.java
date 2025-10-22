package org.tbee.sway;

import net.miginfocom.layout.AC;
import net.miginfocom.layout.AlignX;
import net.miginfocom.layout.AlignY;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.HideMode;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;
import org.tbee.sway.mixin.JComponentMixin;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import java.awt.Component;
import java.util.Collection;
import java.util.function.Consumer;

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


    /**
     * Add a component using no default layout, but returning the CC.
     * @param component
     * Use it like:
     *   sMigPanel.addComponent(new STextField()).growX().wrap()
     *   sMigPanel.addComponent(new STextField())
     * @return this
     */
    public CC addComponent(JComponent component) {
        CC cc = new CC();
        add(component, cc);
        return cc;
    }

    /**
     * Add a component using no default layout, but providing the CC to the provided consumer.
     * In this way multiple addComponent calls can be chained (fluent API).
     * Use it like:
     *   sMigPanel.addComponent(new STextField(), cc -> cc.growX().wrap())
     *            .addComponent(new STextField(), cc -> {})
     * @param component
     * @param consumer
     * @return this
     */
    public SMigPanel addComponent(JComponent component, Consumer<CC> consumer) {
        CC cc = new CC();
        add(component, cc);
        consumer.accept(cc);
        return this;
    }

    /**
     * Add a component using the default label layout (align baseline right).
     * If you do not like it, just add a SLabel normally, with your own CC.
     *
     * @param text
     * @return
     */
    public CC addLabel(String text) {
        return addLabel(SLabel.of(text));
    }

    /**
     * Add a component using the default label layout (align baseline right).
     * If you do not like it, just add the component normally, with your own CC.
     * @param component
     * @return
     */
    public CC addLabel(JComponent component) {
        CC cc = new CC() //
                .alignX(AlignX.TRAILING) //
                .alignY(AlignY.BASELINE);
        add(component, cc);
        return cc;
    }

    /**
     * Add a component using the default label layout (align baseline right).
     * If you do not like it, just add the component normally, with your own CC.
     * @param component
     * @param consumer
     * @return
     */
    public SMigPanel addLabel(JComponent component, Consumer<CC> consumer) {
        consumer.accept(addLabel(component));
        return this;
    }

    /**
     * Add a component using the default field layout (align top left).
     * If you do not like it, just add the component normally, with your own CC.
     * @param component
     * @return
     */
    public CC addField(JComponent component) {
        CC cc = new CC() //
                .alignX(AlignX.LEADING) //
                .alignY(AlignY.TOP);
        add(component, cc);
        return cc;
    }

    /**
     * Add a component using the default field layout (align top left).
     * If you do not like it, just add the component normally, with your own CC.
     * @param component
     * @param consumer
     * @return
     */
    public SMigPanel addField(JComponent component, Consumer<CC> consumer) {
        consumer.accept(addField(component));
        return this;
    }

    /**
     * Add both a label and field side by side, using the default layouts.
     * The additional fields will be placed into the same cell as the field, by using split on the CC of the field.
     * Additional fields can for example be a qualifier SLabel, e.g. "length: [field] meter"
     * @param labelComponent
     * @param fieldComponent
     * @param additionalComponents
     * @return CC of field component (not of the last additionalComponents, so e.g. span works, but wrap probably causes an unexpected layout).
     */
    public CC addLabelAndField(JComponent labelComponent, JComponent fieldComponent, JComponent... additionalComponents) {
    	CC labelCC = addLabel(labelComponent);

        // Automatically switch label to alignY top if the field is larger than roughly 1 line
        AlignY labelAlignY = AlignY.BASELINE;
        if (fieldComponent instanceof JScrollPane || fieldComponent.getPreferredSize().height > (1.1 * STextField.ofString().value("X").getPreferredSize().height)) { // if (fieldComponent instanceof STextArea)
        	labelAlignY = AlignY.TOP;
        }
        labelCC.alignY(AlignY.TOP);

        CC fieldCC = addField(fieldComponent);
        if (additionalComponents.length > 0) {
            fieldCC.split(additionalComponents.length + 1);
            for (int i = 0; i < additionalComponents.length; i++) {

                // align like a label
                if (additionalComponents[i] instanceof JLabel
                 || additionalComponents[i] instanceof JButton
                 || additionalComponents[i] instanceof JCheckBox) {
                    addLabel(additionalComponents[i]).alignY(labelAlignY);
                }
                else {
                    addField(additionalComponents[i]);
                }
            }
        }
        return fieldCC;
    }

    /**
     * Add both a label and field side by side, using the default layouts.
     * The additional fields will be placed into the same cell as the field, by using split on the CC of the field.
     * Additional fields can for example be a qualifier SLabel, e.g. "length: [field] meter"
     * @param label
     * @param fieldComponent
     * @param additionalFieldComponents
     * @return CC of field component (not of the last additionalComponents, so e.g. span works, but wrap probably causes an unexpected layout).
     */
    public CC addLabelAndField(String label, JComponent fieldComponent, JComponent... additionalFieldComponents) {
    	return addLabelAndField(new SLabel(label), fieldComponent, additionalFieldComponents);
    }

    /**
     * Add both a label and field side by side, using the default layouts.
     * The additional fields will be placed into the same cell as the field, by using split on the CC of the field.
     * Additional fields can for example be a qualifier SLabel, e.g. "length: [field] meter"
     * @param label
     * @param fieldComponent
     * @param consumer CC of field component (not of the last additionalComponents, so e.g. span works, but wrap probably causes an unexpected layout).
     * @param additionalComponents
     * @return this
     */
    public SMigPanel addLabelAndField(String label, JComponent fieldComponent, Consumer<CC> consumer, JComponent... additionalComponents) {
        consumer.accept(addLabelAndField(label, fieldComponent, additionalComponents));
        return this;
    }

    /**
     * Add both a label and field side by side, using the default layouts.
     * The additional fields will be placed into the same cell as the field, by using split on the CC of the field.
     * Additional fields can for example be a qualifier SLabel, e.g. "length: [field] meter"
     * @param labelComponent
     * @param fieldComponent
     * @param consumer CC of field component (not of the last additionalComponents, so e.g. span works, but wrap probably causes an unexpected layout).
     * @param additionalComponents
     * @return this
     */
    public SMigPanel addLabelAndField(JComponent labelComponent, JComponent fieldComponent, Consumer<CC> consumer, JComponent... additionalComponents) {
        consumer.accept(addLabelAndField(labelComponent, fieldComponent, additionalComponents));
        return this;
    }

    /**
     * Just changing the CC values does not effectuate them, setCCFor needs to be called.
     * @param component
     * @return
     */
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
    public void updateCCFor(Component component, Consumer<CC> consumer) {
        CC cc = getCCFor(component);
        consumer.accept(cc);
        setCCFor(component, cc);
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
        return reapply(lc.fill());
    }

    public SMigPanel fillX() {
        return reapply(lc.fillX());
    }

    public SMigPanel fillY() {
        return reapply(lc.fillY());
    }

    public SMigPanel noMargins() {
        return reapply(lc.insets("0", "0", "0", "0"));
    }

    public SMigPanel noGaps() {
        return reapply(lc.gridGap("0", "0"));
    }
    
    public SMigPanel alignX(String v) {
        return reapply(lc.alignX(v));
    }

    public SMigPanel alignX(AlignX v) {
        return reapply(lc.alignX(v));
    }

    public SMigPanel alignY(String v) {
        return reapply(lc.alignY(v));
    }
    public SMigPanel alignY(AlignY v) {
        return reapply(lc.alignY(v));
    }

    private SMigPanel reapply(LC lc) {
        migLayout.setLayoutConstraints(lc); // reapply
        return this;
    }

    /**
     * Wrap after the last component
     */
    public SMigPanel wrap() {
        Component component = getComponent(getComponentCount() - 1);
        updateCCFor(component, cc -> cc.wrap());
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

    public SMigPanel skip(int numberOfColumns) {
        if (numberOfColumns > 0) {
            addLabel("").skip(numberOfColumns - 1);
        }
        return this;
    }
}

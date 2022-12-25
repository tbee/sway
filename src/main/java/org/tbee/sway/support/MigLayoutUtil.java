/*
 * Copyright: (c) TBEE.ORG
 * Version:   $Revision: 1.23 $
 * Modified:  $Date: 2011/12/14 16:01:36 $
 * By:        $Author: toeukpap $
 */
package org.tbee.sway.support;

import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class MigLayoutUtil {
	
    /**
     * Get the CC of a component
     *
     * @param component
     */
    static public CC getCC(Component component) {
        return getCC(component.getParent(), component);
    }

    /**
     * Get the CC of a component
     *
     * @param container
     */
    static public CC getCC(Container container, Component component) {
        MigLayout migLayout = (MigLayout) container.getLayout();

        CC cc = (CC) migLayout.getComponentConstraints(component);
        return cc;
    }

    /**
     * Get the CC of a component
     *
     * @param component
     * @param cc
     */
    static public void setCC(Component component, CC cc) {
        Container container = component.getParent();
        MigLayout migLayout = (MigLayout) container.getLayout();

        migLayout.setComponentConstraints(component, cc);
    }


    /**
     * Append a wrap to the component constraints of the last component in the container.
     * The container naturally must use MigLayout.
     *
     * @param container
     */
    static public void wrap(Container container) {
        MigLayout migLayout = (MigLayout) container.getLayout();
        Component component = container.getComponent(container.getComponentCount() - 1);
        CC cc = (CC) migLayout.getComponentConstraints(component);
        if (cc == null) cc = new CC();
        migLayout.setComponentConstraints(component, cc.wrap());
    }

    /**
     * Append a wrap to the component constraints of the last component in the container.
     * The container naturally must use MigLayout.
     *
     * @param container
     */
    static public void skip(Container container, int count) {
        MigLayout migLayout = (MigLayout) container.getLayout();
        Component component = container.getComponent(container.getComponentCount() - 1);
        CC cc = (CC) migLayout.getComponentConstraints(component);
        if (cc == null) cc = new CC();
        migLayout.setComponentConstraints(component, cc.skip(count));
    }

    /**
     * Activate debugging
     *
     * @param container
     */
    static public void debug(Container container) {
        debug((MigLayout) container.getLayout());
    }

    /**
     * Activate debugging
     *
     * @param migLayout
     */
    static public void debug(MigLayout migLayout) {
        ((LC) migLayout.getLayoutConstraints()).debug(1000);
    }

    /**
     * Create a jpanel and add the components.
     * The components are laid out horizontally, but are sized to fit the container vertically
     *
     * @param components
     * @return
     */
    static public JPanel createHorizontalYMaxedPanel(Component... components) {
        // create
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new MigLayout(new LC().fillY()));
        CC cc = new CC().growY().pushY();

        // add
        for (Component component : components) {
            jPanel.add(component, cc);
        }

        // done
        return jPanel;
    }

    /**
     * Create a jpanel and add the components.
     * The components are laid out vertically, but are sized to fit the container horizontally
     *
     * @param components
     * @return
     */
    static public JPanel createVerticalXMaxedPanel(Component... components) {
        // create
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new MigLayout(new LC().fillX()));
        CC cc = new CC().growX().pushX().wrap();

        // add
        for (Component component : components) {
            jPanel.add(component, cc);
        }

        // done
        return jPanel;
    }

    /**
     * Create a jpanel and add the components.
     * The components are laid out horizontally, no gaps, the panel fills its space horizontally, the components fill the panel.
     * Intended to fill up and align below another component, e.g. a number of buttons below a jlist.
     *
     * @param components
     * @return
     */
    static public JPanel createHorizontalFillingGrowingPanel(Component... components) {
        // create
        JPanel jPanel = new JPanel();
        jPanel.setLayout(newMigLayoutFillHorNoGaps());
        CC cc = new CC().grow().pushX();

        // add
        for (Component component : components) {
            jPanel.add(component, cc);
        }

        // done
        return jPanel;
    }

	// =============================================================
    // some often used constraints

	/**
	 * @return a default MigLayout (new MigLayout())
	 */
    static public MigLayout newMigLayout() {
        return new MigLayout(MigLayoutUtil.newLCDefault(), MigLayoutUtil.newACColumn(), MigLayoutUtil.newACRow());
    }

	/**
	 * @return a MigLayout with just LC set
	 */
	static public MigLayout newMigLayout(LC lc) {
        return new MigLayout(lc, MigLayoutUtil.newACColumn(), MigLayoutUtil.newACRow());
    }

	/**
	 * @return a MigLayout without gaps
	 */
	static public MigLayout newMigLayoutNoGaps() {
        return new MigLayout(MigLayoutUtil.newLCDefault().gridGap("0", "0").insets("0"), MigLayoutUtil.newACColumnNoGaps(), MigLayoutUtil.newACRowNoGaps());
    }

    /**
     * @return a MigLayout without gaps filling the space
     */
    static public MigLayout newMigLayoutFillNoGaps() {
        return new MigLayout(MigLayoutUtil.newLCDefaultNoOuterMarginNoGaps().fill(), MigLayoutUtil.newACColumnNoGaps(), MigLayoutUtil.newACRowNoGaps());
    }

    /**
     * Used for a vertical stack, sizing the contents to full width
     * @return a MigLayout without gaps filling the horizontal space
     */
    static public MigLayout newMigLayoutFillHorNoGaps() {
        return new MigLayout(MigLayoutUtil.newLCDefaultNoOuterMarginNoGaps().fillX(), MigLayoutUtil.newACColumnNoGaps(), MigLayoutUtil.newACRowNoGaps());
    }

    /**
     * @return a new LC
     */
    static public LC newLC() {
        return new LC();
    }

    /**
     * @return an LC where the invisible components are present, but sized 0x0
     */
    static public LC newLCDefault() {
        return new LC().hideMode(2)/*invisible components are 0x0*/;
    }

    /**
     * @return
     */
    static public LC newLCDefaultNoOuterMargin() {
        return newLCDefault().insets("0", "0", "0", "0");
    }

    /**
     * @return
     */
    static public LC newLCDefaultNoOuterMarginNoGaps() {
        return newLCDefault().insets("0", "0", "0", "0").gridGap("0", "0");
    }

    /**
     * @return
     */
    static public LC newLCDefaultNoGaps() {
        return newLCDefault().gridGap("0", "0");
    }

    /**
     * @return
     */
    static public AC newACColumn() {
        return new AC();
    }

    /**
     * @return
     */
    static public AC newACColumnNoGaps() {
        return newACColumn().gap("0");
    }

    /**
     * @return
     */
    static public AC newACRow() {
        return new AC();
    }

    /**
     * @return
     */
    static public AC newACRowNoGaps() {
        return newACRow().gap("0");
    }

    /**
     * @return
     */
    static public CC newCC() {
        return new CC();
    }

    /**
     * @return aligned right
     */
    static public CC newCCLabel() {
        return new CC().alignX(ALIGNX_RIGHT);
    }

    /**
     * @return aligned top right
     */
    static public CC newCCLabelTop() {
        return newCCLabel().alignY(ALIGNY_TOP);
    }

    /**
     * @return aligned left
     */
    static public CC newCCField() {
        return new CC().alignX(ALIGNX_LEFT);
    }

    /**
     * @return aligned top left
     */
    static public CC newCCFieldTop() {
        return newCCField().alignY(ALIGNY_TOP);
    }

    final static public String ALIGNX_CENTER = "center";
    final static public String ALIGNX_LEFT = "left";
    final static public String ALIGNX_RIGHT = "right";

    final static public String ALIGNY_CENTER = "center";
    final static public String ALIGNY_TOP = "top";
    final static public String ALIGNY_BOTTOM = "bottom";

    final static public int HIDEMODE_AS_IF_VISIBLE = 0;
    final static public int HIDEMODE_SIZE_0_GAPS_NOT = 1;
    final static public int HIDEMODE_SIZE_0_GAPS_0 = 2;
    final static public int HIDEMODE_IGNORE_COMPLETELY = 3;
}

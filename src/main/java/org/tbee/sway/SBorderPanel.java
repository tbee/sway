package org.tbee.sway;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * JPanel with BorderLayout
 */
public class SBorderPanel extends JPanel {

    public SBorderPanel() {
        super();
        setLayout(new BorderLayout());
    }

    public SBorderPanel(JComponent centerComponent) {
        this();
        center(centerComponent);
    }

    // =========================================================================
    // FLUENT API

    public SBorderPanel center(JComponent component) {
        super.add(component, BorderLayout.CENTER);
        return this;
    }

    public SBorderPanel north(JComponent component) {
        super.add(component, BorderLayout.NORTH);
        return this;
    }

    public SBorderPanel east(JComponent component) {
        super.add(component, BorderLayout.EAST);
        return this;
    }

    public SBorderPanel south(JComponent component) {
        super.add(component, BorderLayout.SOUTH);
        return this;
    }

    public SBorderPanel west(JComponent component) {
        super.add(component, BorderLayout.WEST);
        return this;
    }

    /**
     * Similar to north, but ComponentOrientation will change this
     * @param component
     * @return
     */
    public SBorderPanel pageStart(JComponent component) {
        super.add(component, BorderLayout.PAGE_START);
        return this;
    }

    /**
     * Similar to south, but ComponentOrientation will change this
     * @param component
     * @return
     */
    public SBorderPanel pageEnd(JComponent component) {
        super.add(component, BorderLayout.PAGE_END);
        return this;
    }

    /**
     * Similar to west, but ComponentOrientation will change this
     * @param component
     * @return
     */
    public SBorderPanel lineStart(JComponent component) {
        super.add(component, BorderLayout.LINE_START);
        return this;
    }

    /**
     * Similar to east, but ComponentOrientation will change this
     * @param component
     * @return
     */
    public SBorderPanel lineEnd(JComponent component) {
        super.add(component, BorderLayout.LINE_END);
        return this;
    }
    
    // =========================================================================
    // OF

    /**
     * 
     * @param center
     * @return
     */
    static public SBorderPanel of(JComponent center) {
    	return new SBorderPanel(center);
    }
}

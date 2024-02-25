package org.tbee.sway;

import org.tbee.sway.mixin.ComponentMixin;

import java.awt.LayoutManager;

public class SPanel extends SPanelExtendable<SPanel> implements
        ComponentMixin<SPanel> {

    public SPanel() {
    }

    public SPanel(LayoutManager layout) {
        super(layout);
    }

    // =========================================================================
    // FLUENT API

}

package org.tbee.sway;

import org.tbee.sway.mixin.JComponentMixin;

import java.awt.LayoutManager;

public class SPanel extends SPanelExtendable<SPanel> implements
        JComponentMixin<SPanel> {

    public SPanel() {
    }

    public SPanel(LayoutManager layout) {
        super(layout);
    }

    // =========================================================================
    // FLUENT API

}

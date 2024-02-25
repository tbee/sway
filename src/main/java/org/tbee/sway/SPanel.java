package org.tbee.sway;

import org.tbee.sway.mixin.PropertyChangeListenerMixin;

import java.awt.LayoutManager;

public class SPanel extends SPanelExtendable<SPanel>
implements PropertyChangeListenerMixin<SPanel> {

    public SPanel() {
    }

    public SPanel(LayoutManager layout) {
        super(layout);
    }

    // =========================================================================
    // FLUENT API

}

package org.tbee.sway;

import org.tbee.sway.support.HAlign;

import java.awt.BorderLayout;

public class SBusyOverlay extends SBlockingOverlay {

    public SBusyOverlay() {
        super();
        setLayout(new BorderLayout());
        add(SLabel.of(SIconRegistry.find(SIconRegistry.SwayInternallyUsedIcon.OVERLAY_BUSY)).hAlign(HAlign.CENTER), BorderLayout.CENTER);
    }
}

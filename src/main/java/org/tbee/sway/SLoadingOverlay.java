package org.tbee.sway;

import org.tbee.sway.support.HAlign;

import java.awt.BorderLayout;

public class SLoadingOverlay extends SBlockingOverlay {

    public SLoadingOverlay() {
        super();
        setLayout(new BorderLayout());
        add(SLabel.of(SIconRegistry.find(SIconRegistry.SwayInternallyUsedIcon.OVERLAY_LOADING)).hAlign(HAlign.CENTER), BorderLayout.CENTER);
    }
}

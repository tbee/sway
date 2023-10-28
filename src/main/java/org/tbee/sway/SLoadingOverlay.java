package org.tbee.sway;

import org.tbee.sway.support.HAlign;
import org.tbee.sway.support.IconRegistry;

import java.awt.BorderLayout;

public class SLoadingOverlay extends SBlockingOverlay {

    public SLoadingOverlay() {
        super();
        setLayout(new BorderLayout());
        add(SLabel.of(IconRegistry.find(IconRegistry.SwayInternallyUsedIcon.OVERLAY_LOADING)).hAlign(HAlign.CENTER), BorderLayout.CENTER);
    }
}

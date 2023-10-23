package org.tbee.sway;

import org.tbee.sway.support.IconRegistry;

public class SLoadingOverlay extends SBlockingOverlay {

    private final SLabel label = SLabel.of();

    public SLoadingOverlay() {
        super(IconRegistry.find(IconRegistry.SwayInternallyUsedIcon.OVERLAY_LOADING));
    }
}

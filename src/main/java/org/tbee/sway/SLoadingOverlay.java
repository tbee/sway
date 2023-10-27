package org.tbee.sway;

import org.tbee.sway.support.HAlign;
import org.tbee.sway.support.IconRegistry;

import java.awt.BorderLayout;
import java.awt.Component;

public class SLoadingOverlay extends SBlockingOverlay {

    public SLoadingOverlay(Component component) {
        super(component);
        setLayout(new BorderLayout());
        add(SLabel.of(IconRegistry.find(IconRegistry.SwayInternallyUsedIcon.OVERLAY_LOADING)).hAlign(HAlign.CENTER), BorderLayout.CENTER);
    }
}

package org.tbee.sway;

import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import javax.swing.JPanel;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;

public class SOverlay extends JPanel {

    private Map<Component, ComponentListener> componentListeners = new WeakHashMap<>();

    final private LC lc = new LC();
    final private AC rowAC = new AC();
    final private AC colAC = new AC();
    final private MigLayout migLayout = new MigLayout(lc, colAC, rowAC);


    public SOverlay() {
        setLayout(migLayout);
        setOpaque(false);
        setVisible(true);
    }

    // =========================================================================
    // overlayWith and removeOverlay

    interface OverlayProvider {
        Component getGlassPane();

        default SOverlay getOverlay() {
            return (SOverlay) getGlassPane();
        }
    }

    /**
     * Components that are added later are drawn on top of previous components.
     * @param component
     * @param overlayComponent
     */
    static public void overlayWith(Component component, Component overlayComponent) {
        findOverlayProviderAndCall(component, overlayComponent, overlayProvider -> overlayWith(component, overlayComponent, overlayProvider));
    }

    static void overlayWith(Component component, Component overlayComponent, OverlayProvider overlayProvider) {

        // Make overlay visible
        SOverlay overlay = overlayProvider.getOverlay();
        overlay.setVisible(true);

        // Add component to overlay and position it for the first time
        overlay.add(overlayComponent, new CC(), 0); // index 0 means newer components are drawn on top of previously added
        overlay(component, overlayComponent, overlayProvider);

        // Create the listener to update the overlay
        ComponentListener componentListener = new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                //System.out.println("componentResized " + e.getComponent());
                overlay(component, overlayComponent, overlayProvider);
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                //System.out.println("componentMoved " + e.getComponent());
                overlay(component, overlayComponent, overlayProvider);
            }
        };

        // Register the listener to all layers
        overlay.componentListeners.put(overlayComponent, componentListener);
        Component scanner = component;
        while (scanner != null) {
            scanner.addComponentListener(componentListener);
            scanner = scanner.getParent();
        }
    }

    static public void removeOverlay(Component component, Component overlayComponent) {
        findOverlayProviderAndCall(component, overlayComponent, overlayProvider -> removeOverlay(component, overlayComponent, overlayProvider));
    }

    static void removeOverlay(Component component, Component overlayComponent, OverlayProvider overlayProvider) {

        // Remove component from overlay
        SOverlay overlay = (SOverlay)overlayProvider.getGlassPane();
        overlay.remove(overlayComponent);

        // Find the listener and remove it from all layers
        ComponentListener componentListener = overlay.componentListeners.remove(overlayComponent);
        Component scanner = component;
        while (scanner != null) {
            scanner.removeComponentListener(componentListener);
            scanner = scanner.getParent();
        }
    }

    static void findOverlayProviderAndCall(Component component, Component overlayComponent, Consumer<OverlayProvider> call) {
        Component overlayProviderCandidate = component;
        while (overlayProviderCandidate != null) {
            if (overlayProviderCandidate instanceof OverlayProvider overlayProvider) {
                call.accept(overlayProvider);
                return;
            }
            overlayProviderCandidate = overlayProviderCandidate.getParent();
        }
        throw new IllegalStateException("To-be-overlaid component is not part of a hierarchy with an overlay provider (e.g. SFrame or SDialog) at the top");
    }

    static void overlay(final Component originalComponent, final Component overlayComponent, OverlayProvider overlayProvider) {

        // Determine the location of the overlay
        SOverlay overlay = overlayProvider.getOverlay();
        Point overlayLocation = overlay.getLocationOnScreen();
        Point originalComponentLocation = originalComponent.getLocationOnScreen();
        Point location = new Point(originalComponentLocation.x- overlayLocation.x, originalComponentLocation.y - overlayLocation.y);

        // Position the overlay
        Dimension size = originalComponent.getSize();
        overlay.migLayout.setComponentConstraints(overlayComponent, new CC().pos(location.x + "px", location.y + "px", (location.x + size.width) + "px",(location.y + size.height) + "px"));
    }
}

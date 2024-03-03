package org.tbee.sway;

import org.tbee.sway.mixin.JComponentMixin;

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

/**
 * This pane handles painting overlay components over other components in the glasspane of a SFrame of SDialog.
 * Overlay components can be added and removed as needed, using overlayWith() and removeOverlay().
 */
public class SOverlayPane extends JPanel implements
        JComponentMixin<SOverlayPane> {

    final private Map<Component, ComponentListener> componentListeners = new WeakHashMap<>();


    public SOverlayPane() {
        setLayout(null); // manual layout
        setOpaque(false);
        setVisible(true);
        //setBorder(BorderFactory.createLineBorder(Color.GREEN, 5));
    }

    // =========================================================================
    // overlayWith and removeOverlay

    /**
     * This interface is to allow an overlay to get access to the overlaid component
     */
    interface OverlaidComponentCallback {
        void setComponent(Component component);
    }
    /**
     * This interface is to allow an overlay to do setup
     */
    interface OnOverlayCallback {
        void onOverlay();
    }
    /**
     * This interface is to allow an overlay to do cleanup
     */
    interface OnRemoveCallback {
        void onRemove();
    }

    interface OverlayProvider {
        Component getGlassPane();

        default SOverlayPane getOverlayPane() {
            return (SOverlayPane) getGlassPane();
        }
    }

    /**
     * Overlays that are added later (in call sequence) are drawn on top of earlier overlays.
     * @param component
     * @param overlayComponent
     */
    static public void overlayWith(Component component, Component overlayComponent) {
        findOverlayProviderAndCall(component, overlayComponent, overlayProvider -> overlayWith(component, overlayComponent, overlayProvider));
    }

    static void overlayWith(Component component, Component overlayComponent, OverlayProvider overlayProvider) {

        // Callback
        if (overlayComponent instanceof OverlaidComponentCallback overlaidComponentCallback) {
            overlaidComponentCallback.setComponent(component);
        }

        // Make sure overlayPane is visible
        SOverlayPane overlayPane = overlayProvider.getOverlayPane();
        overlayPane.setVisible(true);

        // Add component to overlayPane and position it for the first time
        //((JComponent)overlayComponent).setBorder(BorderFactory.createLineBorder(Color.BLUE, 3));
        overlayPane.add(overlayComponent, 0); // index 0 means newer components are drawn on top of previously added
        overlay(component, overlayComponent, overlayProvider);
        if (overlayComponent instanceof OnOverlayCallback callback) {
            callback.onOverlay();
        }

        // Create the listener to update the overlayPane
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
        overlayPane.componentListeners.put(overlayComponent, componentListener);
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

        // Remove component from overlayPane
        SOverlayPane overlayPane = overlayProvider.getOverlayPane();
        overlayPane.remove(overlayComponent);
        if (overlayComponent instanceof OnRemoveCallback callback) {
            callback.onRemove();
        }
        component.repaint();

        // Find the listener and remove it from all layers
        ComponentListener componentListener = overlayPane.componentListeners.remove(overlayComponent);
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

        // Determine the location of the overlayPane
        SOverlayPane overlayPane = overlayProvider.getOverlayPane();
        Point overlayLocation = overlayPane.getLocationOnScreen();
        Point originalComponentLocation = originalComponent.getLocationOnScreen();
        Point location = new Point(originalComponentLocation.x- overlayLocation.x, originalComponentLocation.y - overlayLocation.y);

        // Place the overlayComponent
        Dimension size = originalComponent.getSize();
        overlayComponent.setLocation(location);
        overlayComponent.setSize(size);
        if (overlayComponent.isVisible()) {
            // No repainting or invalidating updates the UI as well as this toggle
            overlayComponent.setVisible(false);
            overlayComponent.setVisible(true);
        }
    }
}

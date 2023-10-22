package org.tbee.sway;

import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;

public class SOverlay extends JPanel {

    Map<Component, ComponentListener> componentListeners = new WeakHashMap<>();

    interface OverlayProvider {
        Component getGlassPane();
    }

    static void overlayWith(Component component, Component overlayComponent) {
        findOverlayProviderAndCall(component, overlayComponent, overlayProvider -> overlayWith(component, overlayComponent, overlayProvider));
    }

    static void overlayWith(Component component, Component overlayComponent, OverlayProvider overlayProvider) {

        // Make overlay visible
        SOverlay overlay = (SOverlay)overlayProvider.getGlassPane();
        overlay.setVisible(true);

        // Add component to overlay and position it for the first time
        overlay.add(overlayComponent, new CC());
        overlay(component, overlayComponent);

        // Create the listener to update the overlay
        ComponentListener componentListener = new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                //System.out.println("componentResized " + e.getComponent());
                overlay(component, overlayComponent);
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                //System.out.println("componentMoved " + e.getComponent());
                overlay(component, overlayComponent);
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

    static void removeOverlay(Component component, Component overlayComponent) {
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
        throw new IllegalStateException("To-be-overlaid component is not part of a hierarchy with an overlay provider (e.g. SFrame or SDialog)");
    }


    public SOverlay() {
        setLayout(migLayout);
        setOpaque(false);
        setVisible(true);
    }
    final private LC lc = new LC();
    final private AC rowAC = new AC();
    final private AC colAC = new AC();
    final private MigLayout migLayout = new MigLayout(lc, colAC, rowAC);

    static public void overlay(Component originalComponent, Component overlayComponent) {

        // Summarize the location by adding all the offsets
        Component parent = originalComponent;
        Point loc = parent.getLocation();
        while (parent.getParent() != null && !(parent.getParent() instanceof JRootPane)) {
            parent = parent.getParent();
            Point loc2 = parent.getLocation();
            loc = new Point(loc.x + loc2.x, loc.y + loc2.y);
        }

        Container container = findToplevelContainer(overlayComponent);
//TBEERNOT
        Component glassPane = ((JFrame) container).getGlassPane();
        MigLayout migLayout = (MigLayout) ((JPanel)glassPane).getLayout();
        Point location = loc;
        Dimension size = originalComponent.getSize();
        migLayout.setComponentConstraints(overlayComponent, new CC().pos(location.x + "px",location.y + "px", (location.x + size.width) + "px",(location.y + size.height) + "px"));
    }

    static public Container findToplevelContainer(Component c)
    {
        if (c == null) return null;
        while (c.getParent() != null) {
            c = c.getParent();
        }
        return (c instanceof Container ? (Container)c : null);
    }

    public static void main(String[] args) {

        SPanel frameContentPanel2 = new SPanel().name("frameContentPanel2");
        frameContentPanel2.setLayout(new MigLayout(new LC().fill()));
        SButton showButton = new SButton("Show");
        frameContentPanel2.add(showButton, new CC().gapBefore("10px"));

        SPanel frameContentPanel = new SPanel().name("frameContentPanel");
        frameContentPanel.setLayout(new MigLayout(new LC().fill())); // .debug()
        frameContentPanel.add(new JLabel("Overlay Example"), new CC());
        frameContentPanel.add(frameContentPanel2, new CC().gapBefore("15px").grow());
        frameContentPanel.add(new JButton("No-op"), new CC());

        // overlay with a button
        SButton hideButton = new SButton("Hide");
        showButton.addActionListener(e -> {
            hideButton.setVisible(true);
        });
        hideButton.addActionListener(e -> {
            hideButton.setVisible(false);
        });

        SFrame sFrame = SFrame.of(frameContentPanel);
        sFrame.pack();
        sFrame.setLocationRelativeTo(null);

        showButton.overlayWith(hideButton);
        hideButton.setVisible(false);

        sFrame.setVisible(true);
    }
}

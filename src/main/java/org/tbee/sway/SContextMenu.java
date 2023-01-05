package org.tbee.sway;

import org.tbee.sway.action.Action;
import org.tbee.sway.action.ActionRegistry;

import javax.swing.JPopupMenu;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class SContextMenu {
    static private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SContextMenu.class);

    /**
     *
     */
    synchronized static public void install() {
        if (singleton == null) {
            singleton = new SContextMenu();
            Toolkit.getDefaultToolkit().addAWTEventListener(event -> singleton.showPopupMenu(event), AWTEvent.MOUSE_EVENT_MASK);
        }
    }
    static private SContextMenu singleton = null;

    /**
     *
     */
    public void showPopupMenu(AWTEvent event)
    {
        // interested only in mouse events
        if (!(event instanceof MouseEvent)) {
            return;
        }
        final MouseEvent mouseEvent = (MouseEvent) event;

        // interested only in popuptriggers
        if (!mouseEvent.isPopupTrigger()) {
            return;
        }

        // at some mysterious moment a new EDT thread is made active
        // but our events are still on the old thread and that causes problems in EDT checkers
        // so we explicitly moved this code to the active EDT thread
        javax.swing.SwingUtilities.invokeLater(() -> {

            // getDeepestComponentAt returns the heavy weight component on which the event occurred
            Component component = getDeepestComponentAt(mouseEvent);
            if (logger.isDebugEnabled()) logger.debug("popup over: " + component);
            if (component == null || component.getParent() == null || component.isShowing() == false) {
                return;
            }
            showPopupMenuFor(mouseEvent, component);
        });
    }

    private Component getDeepestComponentAt(MouseEvent mouseEvent)
    {
        // getDeepestComponentAt returns the heavy weight component on which the event occured
        if (mouseEvent.getComponent() == null) {
            return null;
        }
        Component component = SwingUtilities.getDeepestComponentAt(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
        if (component instanceof JViewport) {
            component = ((JViewport)component).getView();
        }
        return component;
    }

    private void showPopupMenuFor(MouseEvent mouseEvent, Component component) {
        Map<String, Object> context = Map.of("MouseEvent", mouseEvent);
        List<Action> actions = ActionRegistry.findFor(component, context);

        JPopupMenu menu = new JPopupMenu();
        actions.stream() //
                .sorted(Comparator.comparingInt(Action::order)) //
                .forEach(a -> {
                    SMenuItem menuItem = new SMenuItem() //
                            .text(a.label()) //
                            .icon(a.icon()) //
                            .enabled(a.isEnabled(component, context)) //
                            .onAction((evt) -> a.apply(component, context));
                    menu.add(menuItem);
                });
        menu.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
    }
}

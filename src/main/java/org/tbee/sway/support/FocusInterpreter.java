package org.tbee.sway.support;

import org.tbee.util.SoftArrayList;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;

/**
 * This class interpretes keyboard focus movements and determines if the focus is lost
 * - to a component within the same application (LOSING_FOCUS followed by a FOCUS_LOST)
 * - or to something outside the application (just LOSING_FOCUS).
 * This allows for more fine tuned behaviour in JTable by force writing an editing cell on FOCUS_LOST.
 * Special consideration is take for popup menus: if though it would be a FOCUS_LOST, this is degraded to a LOSING_FOCUS.
 * <p>
 * All components in the hierarchy to the one actually having focus are assumed to have focus also.
 * This could be a JPanel with a component or a JTable where an editor is active.
 * <p>
 * Listeners for this information must register using addFocusInterpreterListener( FocusInterpreter.FocusInterpreterListener )
 * <p>
 * This component weak-binds itself to the component, so that if the component could be garbage collected, the FocusInterpreter does not block that.
 * On the next focus event, the FocusInterpreter will detect that the component is gone and will remove itself from the listeners and thus be garbage collected itself.
 */
public class FocusInterpreter implements PropertyChangeListener {

    // ================================================================================================
    // CONSTRUCTOR

    /**
     * remember the component and start listening
     */
    public FocusInterpreter(Component component) {
        // we use a WeakReference so this object won't block releasing the component
        // this can happen because we are registered as a property change listener to the focus manager
        reference = new WeakReference<Component>(component);
        start();
    }

    volatile private Reference<Component> reference = null;


    // ================================================================================================
    // KEYBOARD FOCUS LISTENER

    /**
     * start interpreting focus events
     */
    synchronized public void start() {
        if (registered) {
            return;
        }
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("focusOwner", this);
        registered = true;
    }

    volatile private boolean registered = false;

    /**
     * stop interpreting focus events
     */
    synchronized public void stop() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removePropertyChangeListener("focusOwner", this);
        registered = false;
    }

    /**
     *
     */
    public boolean isActive() {
        return registered;
    }

    /**
     * Interprete focus events
     */
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {

        // get the component (if still valid)
        Component component = reference.get();
        if (component == null) {
            stop();
            return;
        }

        // some data to work with
        Component componentTopLevel = SwingUtilities.getRoot(component);
        Component oldComponent = (Component) propertyChangeEvent.getOldValue();
        Component oldComponentTopLevel = (oldComponent == null ? null : SwingUtilities.getRoot(oldComponent));
        Component newComponent = (Component) propertyChangeEvent.getNewValue();
        Component newComponentTopLevel = (newComponent == null ? null : SwingUtilities.getRoot(newComponent));
        //System.out.println("" + iComponent.hashCode() + "/ old " + (oldComponent == null ? "null" : "" + oldComponent.getClass().getName() + oldComponent.hashCode()));
        //System.out.println("" + iComponent.hashCode() + "/ new " + (newComponent == null ? "null" : "" + newComponent.getClass().getName() + newComponent.hashCode()));

        // if we gained focus
        if (newComponentTopLevel == componentTopLevel // within same window
                && newComponent != null // we have a new component
                && (SwingUtilities.isDescendingFrom(newComponent, component) || newComponent == component) // self or below, then we are gaining
                && (state == State.LOSING_FOCUS || state == State.FOCUS_LOST) // we currently not have FOCUS
        ) {
            // remember
            setState(State.GAINED_FOCUS);
            fireFocusEvent(state, oldComponent, newComponent);

            // optimize
            return;
        }

        // if we are losing focus
        if (oldComponentTopLevel == componentTopLevel // within same window
                && oldComponent != null // we have an old component
                && (SwingUtilities.isDescendingFrom(oldComponent, component) || oldComponent == component) // self or below, then we are losing
                && state == State.GAINED_FOCUS // we currently have FOCUS
        ) {
            // remember
            setState(State.LOSING_FOCUS);
            fireFocusEvent(state, oldComponent, newComponent);

            // if the top level window is not visible anymore
            //System.out.println(lComponent.hashCode() + " componentTopLevel.visible=" + (componentTopLevel == null ? "null" : componentTopLevel.isVisible()));
            //			if (componentTopLevel != null && !componentTopLevel.isVisible())
            if (!SwayUtil.isComponentVisibleToUser(component)) {
                // immediately go to focus lost
                setState(State.FOCUS_LOST);
                fireFocusEvent(state, oldComponent, newComponent);
            }

            // optimize
            return;
        }

        // if a component gained focus not below our component but still in the application
        if ((newComponentTopLevel == componentTopLevel || componentTopLevel == null) // within same window or we do not have a parent
                && newComponent != null // we have a new component
                && !(SwingUtilities.isDescendingFrom(newComponent, component) || newComponent == component) // not self or below, then we have lost
                && getState() == State.LOSING_FOCUS // we currently are LOSING focus
        ) {
            // if the focus is lost to a popupmenu,
            // then that came up on our editor and so the focus can stay put
            if (newComponent instanceof JComponent) {
                List lComponents = SwayUtil.flattenComponentTree((JComponent) newComponent);
                for (int i = 0; i < lComponents.size(); i++) {
                    if (lComponents.get(i) instanceof JPopupMenu) {
                        return;
                    }
                }
            }

            // remember
            setState(State.FOCUS_LOST);
            fireFocusEvent(state, oldComponent, newComponent);

            // optimize
            return;
        }
    }


    // ================================================================================================
    // STATE PROPERTY

    /** the current focus state
     *
     * @param v
     */
    public void setState(State v) {
        state = v;
    }
    public State getState() {
        return state;
    }

    private State state = State.FOCUS_LOST;
    public enum State {
        FOCUS_LOST, GAINED_FOCUS, LOSING_FOCUS;
    }

    // ================================================================================================
    // LISTENERS

    private SoftArrayList<FocusInterpreterListener> iListeners = new SoftArrayList<>();

    public void addFocusListener(FocusInterpreterListener l) {
        iListeners.add(l);
    }

    public void removeFocusListener(FocusInterpreterListener l) {
        iListeners.remove(l);
    }

    private void fireFocusEvent(State state, Component oldComponent, Component newComponent) {
        FocusInterpreterEvent lEvent = new FocusInterpreterEvent(state, oldComponent, newComponent);
        iListeners.garbageCollect(); // clean out any dead weak links
        Iterator<FocusInterpreterListener> lIter = iListeners.iterator();
        while (lIter.hasNext()) {
            FocusInterpreterListener lFocusInterpreterListener = lIter.next();
            if (lFocusInterpreterListener != null) {
                lFocusInterpreterListener.focusChanged(lEvent);
            }
        }
    }

    public static interface FocusInterpreterListener {
        public void focusChanged(FocusInterpreterEvent evt);
    }

    public static class FocusInterpreterEvent {
        private State state;
        private Component oldComponent;
        private Component newComponent;

        public FocusInterpreterEvent(State state, Component oldComponent, Component newComponent) {
            this.state = state;
            this.oldComponent = oldComponent;
            this.newComponent = newComponent;
        }

        public State getState() {
            return this.state;
        }

        public Component getOldComponent() {
            return this.oldComponent;
        }

        public Component getNewComponent() {
            return this.newComponent;
        }
    }
}


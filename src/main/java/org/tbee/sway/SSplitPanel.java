package org.tbee.sway;

import org.tbee.sway.mixin.JComponentMixin;
import org.tbee.sway.mixin.PreferencesMixin;
import org.tbee.sway.preference.PreferenceHelper;

import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.Component;
import java.awt.Graphics;

/**
 * SSplitPane.of(components...).vertical()
 */
public class SSplitPanel extends JSplitPane implements
        PreferencesMixin<SSplitPanel>,
        JComponentMixin<SSplitPanel> {

    private final PreferenceHelper preferenceHelper = new PreferenceHelper(this, () -> getNameForPreferences());

    public SSplitPanel() {
        addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorRemoved(AncestorEvent event) {}

            @Override
            public void ancestorMoved(AncestorEvent event) {}

            @Override
            public void ancestorAdded(AncestorEvent event) {
                SwingUtilities.invokeLater(() -> {
                    restorePreferences();
                });
            }
        });
    }

    // ========================================================
    // REMEMBER

    @Override
    public void setPreferencesId(String v) {
        if (v == null) {
            throw new NullPointerException(PREFERENCESID + " cannot be null");
        }
        firePropertyChange(PREFERENCESID, this.nameForPreferences, this.nameForPreferences = v);
    }
    public String getNameForPreferences() {
        return nameForPreferences;
    }
    private String nameForPreferences = "";

    final static private String SEPARATOR_POSITION_PREFERENCE = "SP";

    private void restorePreferences() {
        preferenceHelper.restorePreference(SEPARATOR_POSITION_PREFERENCE, value -> super.setDividerLocation( Integer.parseInt( value ) ));
    }

    @Override
    public void firePropertyChange(String propertyId, int oldValue, int newValue) {
        if (DIVIDER_LOCATION_PROPERTY.equals(propertyId)) {
            preferenceHelper.rememberPreference(SEPARATOR_POSITION_PREFERENCE, () -> "" + getDividerLocation());
        }
        super.firePropertyChange(propertyId, oldValue, newValue);
    }

    @Override protected void paintComponent(Graphics g) {
        preferenceHelper.ignoreChangesToPreferences(() -> super.paintComponent(g));
    }

    @Override public void layout() {
        preferenceHelper.ignoreChangesToPreferences(() -> super.layout());
    }


    // ========================================================
    // FLUENT API

    /**
     * The components are placed vertically, with a horizontal splitter
     */
    public SSplitPanel vertical() {
        setOrientation(JSplitPane.VERTICAL_SPLIT);
        return this;
    }

    /**
     * The components are placed horizontally, with a vertical splitter
     */
    public SSplitPanel horizontal() {
        setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        return this;
    }

    /**
     * The first (aka left) component.
     */
    public SSplitPanel first(Component component) {
        setLeftComponent(component);
        return this;
    }

    /**
     * The second (aka right) component.
     */
    public SSplitPanel second(Component component) {
        setRightComponent(component);
        return this;
    }

    public SSplitPanel dividerLocation(double proportionalLocation) {
        setDividerLocation(proportionalLocation);
        return this;
    }


    // ========================================================
    // OF

    static public SSplitPanel of() {
        return new SSplitPanel();
    }

    static public SSplitPanel of(Component first, Component second) {
        return of().first(first).second(second);
    }
}

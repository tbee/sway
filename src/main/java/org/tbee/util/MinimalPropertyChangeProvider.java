package org.tbee.util;

import java.beans.PropertyChangeListener;

/**
 * The counterpart of the PropertyChangeListener interface.
 * You can use PropertyChangeSupport to easily implement this.
 */
public interface MinimalPropertyChangeProvider
{
    void addPropertyChangeListener(PropertyChangeListener listener);
    void removePropertyChangeListener(PropertyChangeListener listener);
}

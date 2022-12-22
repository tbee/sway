package org.tbee.util;

import java.beans.PropertyChangeListener;

/**
 * Minimal implementation required for binding
 */
public interface MinimalPropertyChangeProvider {
    void addPropertyChangeListener(PropertyChangeListener listener);
    void removePropertyChangeListener(PropertyChangeListener listener);
}

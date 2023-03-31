package org.tbee.sway.support;

import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BeanUtil {

    public record PropertyChangeConnector(Method addPropertyChangeListenerMethod, Method removePropertyChangeListenerMethod) {
        public boolean isComplete() {
            return (addPropertyChangeListenerMethod != null && removePropertyChangeListenerMethod != null);
        }

        public void register(Object bean, PropertyChangeListener propertyChangeListener) {
            try {
                addPropertyChangeListenerMethod.invoke(bean, propertyChangeListener);
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        public void unregister(Object bean, PropertyChangeListener propertyChangeListener) {
            try {
                removePropertyChangeListenerMethod.invoke(bean, propertyChangeListener);
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static public <T> PropertyChangeConnector getPropertyChangeConnector(Class<T> bean) {
        Method addPropertyChangeListenerMethod = null;
        Method removePropertyChangeListenerMethod = null;
        try {
            addPropertyChangeListenerMethod = bean.getMethod("addPropertyChangeListener", new Class<?>[]{PropertyChangeListener.class});
        }
        catch (NoSuchMethodException e) {
            // ignore silently
        }
        try {
            removePropertyChangeListenerMethod = bean.getMethod("removePropertyChangeListener", new Class<?>[]{PropertyChangeListener.class});
        }
        catch (NoSuchMethodException e) {
            // ignore silently
        }
        return new PropertyChangeConnector(addPropertyChangeListenerMethod, removePropertyChangeListenerMethod);
    }
}

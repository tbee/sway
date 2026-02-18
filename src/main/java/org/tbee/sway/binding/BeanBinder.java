package org.tbee.sway.binding;

import com.jgoodies.binding.beans.BeanAdapter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BeanBinder<T> {

    final private BeanAdapter<T> beanAdapter = new BeanAdapter<>(null, true);
    final private List<Consumer<T>> onChangeListeners = new ArrayList<>();
    private int setting = 0;

    public BeanBinder(T bean) {
        beanAdapter.addPropertyChangeListener("bean", evt -> onChangeListeners.forEach(onChangeListener -> onChangeListener.accept((T)evt.getNewValue())));
        set(bean);
    }

    public void set(T bean) {
        setting++;
        try {
            beanAdapter.setBean(bean);
        }
        finally {
            setting--;
        }
    }

    public T get() {
        return beanAdapter.getBean();
    }

    /// returns true if this bean binder is in the process of accepting a new value (true set)
    public boolean isSetting() {
        return setting > 0;
    }

    public BeanBinder<T> onChange(Consumer<T> onChangeListener) {
        onChangeListeners.add(onChangeListener);
        return this;
    }

    /**
     * Do not use this method directly, intended for internal use
     */
    public BeanAdapter<T> getBeanAdapter() {
        return beanAdapter;
    }

    public synchronized void addBeanPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        beanAdapter.addBeanPropertyChangeListener(propertyName, listener);
    }
}

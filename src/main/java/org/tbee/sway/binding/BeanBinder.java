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

    public BeanBinder(T bean) {
        beanAdapter.addPropertyChangeListener("bean", evt -> onChangeListeners.forEach(onChangeListener -> onChangeListener.accept((T)evt.getNewValue())));
        beanAdapter.setBean(bean);
    }

    public void set(T bean) {
        beanAdapter.setBean(bean);
    }

    public T get() {
        return beanAdapter.getBean();
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

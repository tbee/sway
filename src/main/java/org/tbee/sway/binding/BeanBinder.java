package org.tbee.sway.binding;

import com.jgoodies.binding.beans.BeanAdapter;

public class BeanBinder<T> {

    static String VALUE = "value";

    final private BeanAdapter<T> beanAdapter = new BeanAdapter<>(null, true);

    public BeanBinder(T bean) {
        beanAdapter.setBean(bean);
    }

    public void set(T bean) {
        beanAdapter.setBean(bean);
    }

    public T get() {
        return beanAdapter.getBean();
    }

    /**
     * Do not use this method directly, intended for internal use
     */
    public BeanAdapter<T> getBeanAdapter() {
        return beanAdapter;
    }
}

package org.tbee.sway.binding;

import com.jgoodies.binding.beans.BeanAdapter;
import com.jgoodies.binding.beans.PropertyConnector;

public class BeanBinder<T> {

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
     * Do not use this method directly
     * @param propertyName
     * @param bean2
     * @param propertyName2
     */
    public void bind(String propertyName, Object bean2, String propertyName2) {
        PropertyConnector.connectAndUpdate(beanAdapter.getValueModel(propertyName), bean2, propertyName2);
    }
}

package org.tbee.sway.binding;

import com.jgoodies.binding.beans.PropertyConnector;

public class Binding {
    PropertyConnector propertyConnector;

    static Binding of(PropertyConnector propertyConnector) {
        Binding binding = new Binding();
        binding.propertyConnector = propertyConnector;
        return binding;
    }

    /**
     *
     * @param bean
     * @param propertyName
     * @return True if it is bound to this bean & property on either side of the binding
     */
    public boolean bindsTo(Object bean, String propertyName) {
        return propertyConnector.getBean1().equals(bean) && propertyConnector.getProperty1Name().equals(propertyName) //
            || propertyConnector.getBean2().equals(bean) && propertyConnector.getProperty2Name().equals(propertyName);
    }

    public void unbind() {
        propertyConnector.release();
    }
}

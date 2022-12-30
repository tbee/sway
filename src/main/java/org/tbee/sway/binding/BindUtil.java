package org.tbee.sway.binding;

import com.jgoodies.binding.beans.PropertyConnector;

public class BindUtil {

    /**
     * Binds bean & property 1 to bean & property 2.
     * Property 1 will be synced to property 2 after the bind was maded.
     * Use bind(Object bean1, String propertyName1, BeanBinding, PropertyName) to be able to switch beans while keeping the bind.
     *
     * @param bean1
     * @param propertyName1
     * @param bean2
     * @param propertyName2
     * @param exceptionHandler
     * @return
     */
    static public Binding bind(Object bean1, String propertyName1, Object bean2, String propertyName2, ExceptionHandler exceptionHandler) {
        PropertyConnector propertyConnector = PropertyConnector.connect(new ExceptionCatcher(bean1, propertyName1, exceptionHandler), ExceptionCatcher.VALUE, new ExceptionCatcher(bean2, propertyName2, exceptionHandler), ExceptionCatcher.VALUE);
        propertyConnector.updateProperty1();
        return Binding.of(propertyConnector);
    }

    /**
     * Bind to a bean wrapper's property.
     * This will allow the swap the bean (in the BeanBinder) without having to rebind.
     * @param bean1
     * @param propertyName1
     * @param beanBinder
     * @param propertyName
     * @param exceptionHandler
     * @return
     */
    static public Binding bind(Object bean1, String propertyName1, BeanBinder beanBinder, String propertyName, ExceptionHandler exceptionHandler) {
        return bind(bean1, propertyName1, beanBinder.getBeanAdapter().getValueModel(propertyName), "value", exceptionHandler);
    }
}

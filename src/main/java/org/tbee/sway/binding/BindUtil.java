package org.tbee.sway.binding;

import com.jgoodies.binding.beans.PropertyConnector;

import javax.swing.JComponent;
import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class BindUtil {
    static private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(BindUtil.class);

    static private ExceptionHandler loggingExceptionHandler = new ExceptionHandler() {
        @Override
        public boolean handle(Throwable t, JComponent component, Object oldValue, Object newValue) {
            logger.warn(t.getMessage(), t);
            return false;
        }
    };

    /**
     * Binds bean and property 1 to bean and property 2.
     * Property 1 will be synced to property 2 after the bind was added.
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
    static public Binding bind(Object bean1, String propertyName1, Object bean2, String propertyName2, ExceptionHandler exceptionHandler, List<BindChainNode> chain) {
        PropertyConnector propertyConnector = PropertyConnector.connect(new ExceptionCatcher(bean1, propertyName1, exceptionHandler, chain, true), ExceptionCatcher.VALUE, new ExceptionCatcher(bean2, propertyName2, exceptionHandler, chain, false), ExceptionCatcher.VALUE);
        propertyConnector.updateProperty1();
        return Binding.of(propertyConnector);
    }

    /**
     * Binds bean and property 1 to bean and property 2.
     * Property 1 will be synced to property 2 after the bind was maded.
     * Use bind(Object bean1, String propertyName1, BeanBinding, PropertyName) to be able to switch beans while keeping the bind.
     *
     * @param bean1
     * @param propertyName1
     * @param bean2
     * @param propertyName2
     * @return
     */
    static public Binding bind(Object bean1, String propertyName1, Object bean2, String propertyName2) {
        return bind(bean1, propertyName1, bean2, propertyName2, loggingExceptionHandler);
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
        return bind(bean1, propertyName1, beanBinder.getBeanAdapter().getValueModel(propertyName), BeanBinder.VALUE, exceptionHandler);
    }
    static public Binding bind(Object bean1, String propertyName1, BeanBinder beanBinder, String propertyName, ExceptionHandler exceptionHandler, List<BindChainNode> chain) {
        return bind(bean1, propertyName1, beanBinder.getBeanAdapter().getValueModel(propertyName), BeanBinder.VALUE, exceptionHandler, chain);
    }

    /**
     * Bind to a bean wrapper's property.
     * This will allow the swap the bean (in the BeanBinder) without having to rebind.
     * @param bean1
     * @param propertyName1
     * @param beanBinder
     * @param propertyName
     * @return
     */
    static public Binding bind(Object bean1, String propertyName1, BeanBinder beanBinder, String propertyName) {
        return bind(bean1, propertyName1, beanBinder.getBeanAdapter().getValueModel(propertyName), BeanBinder.VALUE, loggingExceptionHandler);
    }
    static public Binding bind(Object bean1, String propertyName1, BeanBinder beanBinder, String propertyName, List<BindChainNode> chain) {
        return bind(bean1, propertyName1, beanBinder.getBeanAdapter().getValueModel(propertyName), BeanBinder.VALUE, loggingExceptionHandler, chain);
    }

    /**
     * Bind to a property change and accept the old and new values
     */
    static public <T> Binding onChange(Object bean1, String propertyName1, BiConsumer<T, T> biconsumer) {
        ValueBean<T> valueBean = new ValueBean<T>();
        valueBean.addPropertyChangeListener(ValueBean.VALUE, pce -> biconsumer.accept((T)pce.getOldValue(), (T)pce.getNewValue()));
        return bind(bean1, propertyName1, valueBean, ValueBean.VALUE, loggingExceptionHandler);
    }

    /**
     * Bind to a property change and accept the new values
     */
    static public <T> Binding onChange(Object bean1, String propertyName1, Consumer<T> consumer) {
        ValueBean<T> valueBean = new ValueBean<T>();
        valueBean.addPropertyChangeListener(ValueBean.VALUE, pce -> consumer.accept((T)pce.getNewValue()));
        return bind(bean1, propertyName1, valueBean, ValueBean.VALUE, loggingExceptionHandler);
    }
}

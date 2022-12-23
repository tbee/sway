package org.tbee.sway.binding;

import com.jgoodies.binding.beans.PropertyAdapter;
import org.tbee.util.AbstractBean;
import org.tbee.util.ExceptionUtil;

public class ExceptionCatcher extends AbstractBean<ExceptionCatcher> {

    @FunctionalInterface
    public interface ExceptionHandler {
        boolean handle(Throwable t, Object oldValue, Object newValue);
    }

    final private PropertyAdapter<Object> propertyAdapter;
    final private ExceptionHandler handler;
    public ExceptionCatcher(Object bean, String property, ExceptionHandler handler) {
        propertyAdapter = new PropertyAdapter<Object>(bean, property, true);
        propertyAdapter.addPropertyChangeListener("value", evt -> {
            firePropertyChange(VALUE, evt.getOldValue(), evt.getNewValue());
        });
        this.handler = handler;
    }

    final static public String VALUE = "value";
    public void setValue(Object v) {
        Object oldValue = propertyAdapter.getValue();
        try {
            propertyAdapter.setValue(v);
        }
        catch (RuntimeException e) {
            Throwable ultimateCause = ExceptionUtil.findUltimateCause(e);
            boolean handled = handler.handle(ultimateCause, oldValue, v);
            if (!handled) {
                throw e;
            }
        }
    }
    public Object getValue() {
        return propertyAdapter.getValue();
    }
}

package org.tbee.sway.binding;

import com.jgoodies.binding.beans.PropertyAdapter;
import org.tbee.util.AbstractBean;
import org.tbee.util.ExceptionUtil;

/**
 * Catches any exception and forwards them to the exception handler
 */
public class ExceptionCatcher extends AbstractBean<ExceptionCatcher> {
    static private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ExceptionCatcher.class);

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
            if (handler != null) {
                boolean handled = handler.handle(ultimateCause, oldValue, v);
                if (handled) {
                    return;
                }
            }
            throw e; // TBEERNOT: check if this exception is swalled, if so log it
        }
    }
    public Object getValue() {
        return propertyAdapter.getValue();
    }
}

package org.tbee.sway.binding;

import com.jgoodies.binding.beans.PropertyAdapter;
import org.tbee.util.AbstractBean;
import org.tbee.util.ExceptionUtil;

import java.util.List;

/**
 * Catches any exception and forwards them to the exception handler
 */
public class ExceptionCatcher extends AbstractBean<ExceptionCatcher> {
    static private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ExceptionCatcher.class);

    final private PropertyAdapter<Object> propertyAdapter;
    final private ExceptionHandler handler;
    final private List<BindChainNode> chain;
    final private boolean binder;

    public ExceptionCatcher(Object bean, String property, ExceptionHandler handler) {
        this(bean, property, handler, null, false);

    }
    public ExceptionCatcher(Object bean, String property, ExceptionHandler handler, List<BindChainNode> chain, boolean binder) {
        propertyAdapter = new PropertyAdapter<Object>(bean, property, true);
        propertyAdapter.addPropertyChangeListener("value", evt -> {
            firePropertyChange(VALUE, evt.getOldValue(), evt.getNewValue());
        });
        this.handler = handler;
        this.chain = chain;
        this.binder = binder;
    }

    final static public String VALUE = "value";
    public void setValue(Object v) {
        Object oldValue = propertyAdapter.getValue();
        try {
            v = pullThroughChain(v);
            propertyAdapter.setValue(v);
        }
        catch (RuntimeException e) {
            Throwable ultimateCause = ExceptionUtil.findUltimateCause(e);
            if (handler != null && handler.handle(ultimateCause, null, oldValue, v)) {
                return;
            }
            throw e;
        }
    }
    public Object getValue() {
        return propertyAdapter.getValue();
    }

    private Object pullThroughChain(Object v) {
        if (chain != null) {
            if (binder) {
                for (int i = 0; i < chain.size(); i++) {
                    v = chain.get(i).binderToBindee(v);
                }
            }
            else {
                for (int i = chain.size() - 1; i >= 0; i--) {
                    v = chain.get(i).bindeeToBinder(v);
                }
            }
        }
        return v;
    }
}

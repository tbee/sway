package org.tbee.sway.binding;

import com.jgoodies.common.bean.Bean;

public class ValueBean<T> extends Bean {
    static String VALUE = "value";

    private T value = null;

    public void setValue(T value) {
        firePropertyChange(VALUE, this.value, this.value = value);
    }

    public T getValue() {
        return value;
    }
}

package org.tbee.sway.mixin;

import org.tbee.sway.binding.BindingEndpoint;
import org.tbee.sway.binding.ExceptionHandler;
import org.tbee.sway.support.VAlign;

import java.awt.Component;

public interface VAlignMixin<T extends Component> {

    int getVerticalAlignment();
    void setVerticalAlignment(int alignment);
    void firePropertyChange(String propertyName, Object oldValue, Object newValue);
    ExceptionHandler getExceptionHandler();

    /**
     * Enum variant of VerticalAlignment
     * @param v
     */
    default void setVAlign(VAlign v) {
        VAlign old = getVAlign();
        setVerticalAlignment(v.getSwingConstant());
        firePropertyChange(VALIGN, old, v);
    }
    default VAlign getVAlign() {
        return VAlign.of(getVerticalAlignment());
    }
    default T vAlign(VAlign v) {
        setVAlign(v);
        return (T)this;
    }
    final static public String VALIGN = "vAlign";
    default BindingEndpoint<VAlign> vAlign$() {
        return BindingEndpoint.of(this, VALIGN, getExceptionHandler());
    }
}

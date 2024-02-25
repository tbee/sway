package org.tbee.sway.mixin;

import org.tbee.sway.binding.BindingEndpoint;
import org.tbee.sway.binding.ExceptionHandler;
import org.tbee.sway.support.HAlign;

import java.awt.Component;

public interface HAlignMixin<T extends Component> {

    int getHorizontalAlignment();
    void setHorizontalAlignment(int alignment);
    void firePropertyChange(String propertyName, Object oldValue, Object newValue);
    ExceptionHandler getExceptionHandler();

    /**
     * Enum variant of HorizontalAlignment
     * @param v
     */
    default void setHAlign(HAlign v) {
        HAlign old = getHAlign();
        setHorizontalAlignment(v.getSwingConstant());
        firePropertyChange(HALIGN, old, v);
    }
    default HAlign getHAlign() {
        return HAlign.of(getHorizontalAlignment());
    }
    default T hAlign(HAlign v) {
        setHAlign(v);
        return (T)this;
    }
    final static public String HALIGN = "hAlign";
    default BindingEndpoint<HAlign> hAlign$() {
        return BindingEndpoint.of(this, HALIGN, getExceptionHandler());
    }
}

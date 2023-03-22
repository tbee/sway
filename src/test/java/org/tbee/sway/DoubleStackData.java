package org.tbee.sway;

import org.tbee.sway.beanGenerator.annotations.Bean;
import org.tbee.sway.beanGenerator.annotations.Property;
import org.tbee.util.AbstractBean;

@Bean(stripSuffix = "Data")
abstract public class DoubleStackData extends AbstractBean<DoubleStack> {
    private final DoubleStack self = (DoubleStack)this;

    @Property
    String name;

    public void custom() {
        self.setName("test");
    }

}

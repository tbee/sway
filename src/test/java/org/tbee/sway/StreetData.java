package org.tbee.sway;

import org.tbee.sway.beanGenerator.annotations.Bean;
import org.tbee.sway.beanGenerator.annotations.Property;
import org.tbee.util.AbstractBean;

@Bean(stripSuffix = "Data")
abstract public class StreetData extends AbstractBean<City> {

    public StreetData() {
    }

    private final Street self = (Street)this;

    static public Street of() {
        return new Street();
    }

    static public Street of(String name) {
        return of()
                .name(name);
    }

    @Property
    String name;

//    @Override
//    public void firePropertyChange(String name, Object before, Object after) {
//        System.out.println(this.getClass().getSimpleName() + "." + name + ": " + before + " -> " + after);
//        super.firePropertyChange(name, before, after);
//    }
}

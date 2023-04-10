package org.tbee.sway;

import org.tbee.sway.beanGenerator.annotations.Bean;
import org.tbee.sway.beanGenerator.annotations.Property;
import org.tbee.util.AbstractBean;

@Bean(stripSuffix = "Data")
abstract public class BuildingData extends AbstractBean<Building> {

    public BuildingData() {
    }

    private final Building self = (Building)this;

    static public Building of() {
        return new Building();
    }

    static public Building of(int number) {
        return of()
                .number(number);
    }

    @Property(includeInToString = true)
    int number;

    @Property
    String suffix;

//    @Override
//    public void firePropertyChange(String name, Object before, Object after) {
//        System.out.println(this.getClass().getSimpleName() + "." + name + ": " + before + " -> " + after);
//        super.firePropertyChange(name, before, after);
//    }
}

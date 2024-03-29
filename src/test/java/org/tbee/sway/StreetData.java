package org.tbee.sway;

import org.tbee.sway.beanGenerator.Scope;
import org.tbee.sway.beanGenerator.annotations.Bean;
import org.tbee.sway.beanGenerator.annotations.Property;
import org.tbee.util.AbstractBean;

import java.util.ArrayList;
import java.util.List;

@Bean(stripSuffix = "Data")
abstract public class StreetData extends AbstractBean<Street> {

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

    @Property(includeInToString = true)
    String name;

    @Property(recordStyleGetter = true, setterScope = Scope.PRIVATE, isList = true, nameSingular = "building")
    List<Building> buildings = new ArrayList<>();

    @Property
    City city;

//    @Override
//    public void firePropertyChange(String name, Object before, Object after) {
//        System.out.println(this.getClass().getSimpleName() + "." + name + ": " + before + " -> " + after);
//        super.firePropertyChange(name, before, after);
//    }
}

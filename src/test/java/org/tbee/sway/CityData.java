package org.tbee.sway;

import org.tbee.sway.beanGenerator.annotations.Bean;
import org.tbee.sway.beanGenerator.annotations.ListProperty;
import org.tbee.sway.beanGenerator.annotations.Property;
import org.tbee.sway.binding.BeanBinder;
import org.tbee.sway.binding.BindingEndpoint;
import org.tbee.util.AbstractBean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Bean(stripSuffix = "Data")
abstract public class CityData extends AbstractBean<CityData> {

    private final City self = (City)this;

    static public City of() {
        return new City();
    }

    static public City of(String name, int distance) {
        return of()
                .name(name)
                .distance(distance);
    }

    /** name: string property */
    @Property(name="name")
    String nameNot;

    /** distance: integer property */
    @Property
    int distance;

    /** distanceInt: a property without actual storage, but forwarding to age */
    public void setDistanceInt(Integer v) {
        self.setDistance(v);
    }
    public Integer getDistanceInt() {
        return self.getDistance();
    }
    static public String DISTANCEINT = "distanceInt";
    public CityData distanceInt(Integer v) {
        setDistanceInt(v);
        return this;
    }

    /** calc: a derived property */
    public Integer getRoundTrip() {
        return distance * 2;
    }
    static public String ROUNDTRIP = "roundTrip";
    public BindingEndpoint<Integer> roundTrip$() {
        return BindingEndpoint.of(this, ROUNDTRIP);
    }
    static public BindingEndpoint<Integer> roundTrip$(BeanBinder<City> beanBinder) {
        return BindingEndpoint.of(beanBinder, ROUNDTRIP);
    }

    /** surface: BigDecimal property */
    @Property
    BigDecimal surfaceInKM2;

    /** growing: boolean property */
    @Property
    boolean growing = true;

    /** cityRights: Boolean property */
    @Property(recordStyleGetter = true, recordStyleSetter = true, recordStyleWither = false)
    Boolean cityRights = null;

    /** sisterCity: City property */
    @Property(includeInToString = false)
    City sisterCity;

    /** sisterCity: City property */
    @ListProperty(nameSingular = "partnerCity", recordStyleGetter = true)
    List<City> partnerCities = new ArrayList<>();

    @Override
    public void firePropertyChange(String name, Object before, Object after) {
        System.out.println(this.getClass().getSimpleName() + "." + name + ": " + before + " -> " + after);
        super.firePropertyChange(name, before, after);
    }
}

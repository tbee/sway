package org.tbee.sway;

import org.tbee.sway.beanGenerator.annotations.Bean;
import org.tbee.sway.beanGenerator.annotations.Property;
import org.tbee.util.AbstractBean;

import java.math.BigDecimal;

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
    @Property
    String name;

    /** distance: integer property */
    @Property
    int distance;
    public org.tbee.sway.binding.BindingEndpoint<java.lang.Integer> distance$() {
        return org.tbee.sway.binding.BindingEndpoint.of(this, "distance");
    }
    static public org.tbee.sway.binding.BindingEndpoint<java.lang.Integer> distance$(org.tbee.sway.binding.BeanBinder<org.tbee.sway.City> beanBinder) {
        return org.tbee.sway.binding.BindingEndpoint.of(beanBinder, "distance");
    }


    /** distanceInt: a property without actual storage, but forwarding to age */
//    public void setDistanceInt(Integer v) {
//        setDistance(v);
//    }
//    public Integer getDistanceInt() {
//        return getDistance();
//    }
//    static public String DISTANCEINT = "distanceInt";
//    public CityData distanceInt(Integer v) {
//        setDistanceInt(v);
//        return this;
//    }

    /** calc: a derived property */
//    public Integer getRoundTrip() {
//        return distance * 2;
//    }
//    static public String ROUNDTRIP = "roundTrip";
//    public BindingEndpoint<Integer> roundTrip$() {
//        return BindingEndpoint.of(this, ROUNDTRIP);
//    }
//    static public BindingEndpoint<Integer> roundTrip$(BeanBinder<CityData> beanBinder) {
//        return BindingEndpoint.of(beanBinder, ROUNDTRIP);
//    }

    /** surface: BigDecimal property */
    @Property
    BigDecimal surfaceInKM2;

    /** growing: boolean property */
    @Property
    boolean growing = true;
    public org.tbee.sway.binding.BindingEndpoint<java.lang.Boolean> growing$() {
        return org.tbee.sway.binding.BindingEndpoint.of(this, "growing");
    }
    static public org.tbee.sway.binding.BindingEndpoint<java.lang.Boolean> growing$(org.tbee.sway.binding.BeanBinder<org.tbee.sway.City> beanBinder) {
        return org.tbee.sway.binding.BindingEndpoint.of(beanBinder, "growing");
    }

    /** cityRights: Boolean property */
    @Property
    Boolean cityRights = null;

    /** sisterCity: City property */
    @Property(includeInToString = false)
    CityData sisterCity;

    @Override
    public void firePropertyChange(String name, Object before, Object after) {
        System.out.println(this.getClass().getSimpleName() + "." + name + ": " + before + " -> " + after);
        super.firePropertyChange(name, before, after);
    }
}

package org.tbee.sway;

import org.tbee.sway.beanGenerator.ListPretendingToHaveAddedItem;
import org.tbee.sway.beanGenerator.Scope;
import org.tbee.sway.beanGenerator.annotations.Property;
import org.tbee.sway.binding.BeanBinder;
import org.tbee.sway.binding.BindingEndpoint;
import org.tbee.util.AbstractBean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class City extends AbstractBean<City> {

    public City() {
        addVetoableChangeListener(DISTANCE, e -> {
            if (((Integer)e.getNewValue()).intValue() < 0) {
                throw new IllegalArgumentException("Distance must be >= 0"); // TBEERNOT how to add this to generated setters?
            }
        } );
    }
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
    public City distanceInt(Integer v) {
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
    @Property(recordStyleGetter = true, setterScope = Scope.PRIVATE, isList = true, nameSingular = "partnerCity")
    List<City> partnerCities = new ArrayList<>();

    @Property(recordStyleGetter = true, setterScope = Scope.PRIVATE, isList = true, nameSingular = "thing")
    List things = new ArrayList<>();

    @Override
    public void firePropertyChange(String name, Object before, Object after) {
        System.out.println(this.getClass().getSimpleName() + "." + name + ": " + before + " -> " + after);
        super.firePropertyChange(name, before, after);
    }

    // ---------------------
    // name
    public java.lang.String getName() {
        return $swayWrap(this.nameNot);
    }
    public void setName(java.lang.String v) {
        fireVetoableChange("name", $swayWrap(this.nameNot), $swayWrap(v));
        firePropertyChange("name", $swayWrap(this.nameNot), $swayWrap(this.nameNot = v));
    }
    public org.tbee.sway.City name(java.lang.String v) {
        setName(v);
        return (org.tbee.sway.City)this;
    }
    public org.tbee.sway.binding.BindingEndpoint<java.lang.String> name$() {
        return org.tbee.sway.binding.BindingEndpoint.of(this, "name");
    }
    static public org.tbee.sway.binding.BindingEndpoint<java.lang.String> name$(org.tbee.sway.binding.BeanBinder<org.tbee.sway.City> beanBinder) {
        return org.tbee.sway.binding.BindingEndpoint.of(beanBinder, "name");
    }
    final static public String NAME = "name";

    // ---------------------
    // distance
    public int getDistance() {
        return $swayWrap(this.distance);
    }
    public void setDistance(int v) {
//        if (v < 0) {
//            throw new IllegalArgumentException("Distance must be >= 0"); // TBEERNOT how to add this to generated setters?
//        }
        fireVetoableChange("distance", $swayWrap(this.distance), $swayWrap(v));
        firePropertyChange("distance", $swayWrap(this.distance), $swayWrap(this.distance = v));
    }
    public org.tbee.sway.City distance(int v) {
        setDistance(v);
        return (org.tbee.sway.City)this;
    }
    public org.tbee.sway.binding.BindingEndpoint<java.lang.Integer> distance$() {
        return org.tbee.sway.binding.BindingEndpoint.of(this, "distance");
    }
    static public org.tbee.sway.binding.BindingEndpoint<java.lang.Integer> distance$(org.tbee.sway.binding.BeanBinder<org.tbee.sway.City> beanBinder) {
        return org.tbee.sway.binding.BindingEndpoint.of(beanBinder, "distance");
    }
    final static public String DISTANCE = "distance";

    // ---------------------
    // surfaceInKM2
    public java.math.BigDecimal getSurfaceInKM2() {
        return $swayWrap(this.surfaceInKM2);
    }
    public void setSurfaceInKM2(java.math.BigDecimal v) {
        fireVetoableChange("surfaceInKM2", $swayWrap(this.surfaceInKM2), $swayWrap(v));
        firePropertyChange("surfaceInKM2", $swayWrap(this.surfaceInKM2), $swayWrap(this.surfaceInKM2 = v));
    }
    public org.tbee.sway.City surfaceInKM2(java.math.BigDecimal v) {
        setSurfaceInKM2(v);
        return (org.tbee.sway.City)this;
    }
    public org.tbee.sway.binding.BindingEndpoint<java.math.BigDecimal> surfaceInKM2$() {
        return org.tbee.sway.binding.BindingEndpoint.of(this, "surfaceInKM2");
    }
    static public org.tbee.sway.binding.BindingEndpoint<java.math.BigDecimal> surfaceInKM2$(org.tbee.sway.binding.BeanBinder<org.tbee.sway.City> beanBinder) {
        return org.tbee.sway.binding.BindingEndpoint.of(beanBinder, "surfaceInKM2");
    }
    final static public String SURFACEINKM2 = "surfaceInKM2";

    // ---------------------
    // growing
    public boolean getGrowing() {
        return $swayWrap(this.growing);
    }
    public void setGrowing(boolean v) {
        fireVetoableChange("growing", $swayWrap(this.growing), $swayWrap(v));
        firePropertyChange("growing", $swayWrap(this.growing), $swayWrap(this.growing = v));
    }
    public org.tbee.sway.City growing(boolean v) {
        setGrowing(v);
        return (org.tbee.sway.City)this;
    }
    public org.tbee.sway.binding.BindingEndpoint<java.lang.Boolean> growing$() {
        return org.tbee.sway.binding.BindingEndpoint.of(this, "growing");
    }
    static public org.tbee.sway.binding.BindingEndpoint<java.lang.Boolean> growing$(org.tbee.sway.binding.BeanBinder<org.tbee.sway.City> beanBinder) {
        return org.tbee.sway.binding.BindingEndpoint.of(beanBinder, "growing");
    }
    final static public String GROWING = "growing";

    // ---------------------
    // cityRights
    public java.lang.Boolean getCityRights() {
        return $swayWrap(this.cityRights);
    }
    public void setCityRights(java.lang.Boolean v) {
        fireVetoableChange("cityRights", $swayWrap(this.cityRights), $swayWrap(v));
        firePropertyChange("cityRights", $swayWrap(this.cityRights), $swayWrap(this.cityRights = v));
    }
    public java.lang.Boolean cityRights() {
        return getCityRights();
    }
    public void cityRights(java.lang.Boolean v) {
        setCityRights(v);
    }
    public org.tbee.sway.binding.BindingEndpoint<java.lang.Boolean> cityRights$() {
        return org.tbee.sway.binding.BindingEndpoint.of(this, "cityRights");
    }
    static public org.tbee.sway.binding.BindingEndpoint<java.lang.Boolean> cityRights$(org.tbee.sway.binding.BeanBinder<org.tbee.sway.City> beanBinder) {
        return org.tbee.sway.binding.BindingEndpoint.of(beanBinder, "cityRights");
    }
    final static public String CITYRIGHTS = "cityRights";

    // ---------------------
    // sisterCity
    public org.tbee.sway.City getSisterCity() {
        return $swayWrap(this.sisterCity);
    }
    public void setSisterCity(org.tbee.sway.City v) {
        fireVetoableChange("sisterCity", $swayWrap(this.sisterCity), $swayWrap(v));
        firePropertyChange("sisterCity", $swayWrap(this.sisterCity), $swayWrap(this.sisterCity = v));
    }
    public org.tbee.sway.City sisterCity(org.tbee.sway.City v) {
        setSisterCity(v);
        return (org.tbee.sway.City)this;
    }
    public org.tbee.sway.binding.BindingEndpoint<org.tbee.sway.City> sisterCity$() {
        return org.tbee.sway.binding.BindingEndpoint.of(this, "sisterCity");
    }
    static public org.tbee.sway.binding.BindingEndpoint<org.tbee.sway.City> sisterCity$(org.tbee.sway.binding.BeanBinder<org.tbee.sway.City> beanBinder) {
        return org.tbee.sway.binding.BindingEndpoint.of(beanBinder, "sisterCity");
    }
    final static public String SISTERCITY = "sisterCity";

    // ---------------------
    // partnerCities
    public java.util.List<org.tbee.sway.City> getPartnerCities() {
        return $swayWrap(this.partnerCities);
    }
    private void setPartnerCities(java.util.List<org.tbee.sway.City> v) {
        fireVetoableChange("partnerCities", $swayWrap(this.partnerCities), $swayWrap(v));
        firePropertyChange("partnerCities", $swayWrap(this.partnerCities), $swayWrap(this.partnerCities = v));
    }
    public java.util.List<org.tbee.sway.City> partnerCities() {
        return getPartnerCities();
    }
    public org.tbee.sway.City partnerCities(java.util.List<org.tbee.sway.City> v) {
        setPartnerCities(v);
        return (org.tbee.sway.City)this;
    }
    public org.tbee.sway.binding.BindingEndpoint<java.util.List<org.tbee.sway.City>> partnerCities$() {
        return org.tbee.sway.binding.BindingEndpoint.of(this, "partnerCities");
    }
    static public org.tbee.sway.binding.BindingEndpoint<java.util.List<org.tbee.sway.City>> partnerCities$(org.tbee.sway.binding.BeanBinder<org.tbee.sway.City> beanBinder) {
        return org.tbee.sway.binding.BindingEndpoint.of(beanBinder, "partnerCities");
    }
    final static public String PARTNERCITIES = "partnerCities";
    public void addPartnerCity(org.tbee.sway.City v) {
        java.util.List<org.tbee.sway.City> pretendedNewValue = $swayWrap((java.util.List<org.tbee.sway.City>)new ListPretendingToHaveAddedItem<City>(this.partnerCities, v));
        fireVetoableChange("partnerCities", getPartnerCities(), pretendedNewValue);
        boolean wasAdded = this.partnerCities.add(v);
        if (wasAdded) {
            java.util.List<org.tbee.sway.City> pretendedOldValue = $swayWrap((java.util.List<org.tbee.sway.City>)new org.tbee.sway.beanGenerator.ListPretendingToHaveRemovedItem<org.tbee.sway.City>(this.partnerCities, v));
            firePropertyChange("partnerCities", pretendedOldValue, getPartnerCities());
        }
    }
    public void removePartnerCity(org.tbee.sway.City v) {
        java.util.List<org.tbee.sway.City> pretendedNewValue = $swayWrap((java.util.List<org.tbee.sway.City>)new org.tbee.sway.beanGenerator.ListPretendingToHaveRemovedItem<org.tbee.sway.City>(this.partnerCities, v));
        fireVetoableChange("partnerCities", getPartnerCities(), pretendedNewValue);
        boolean wasRemoved = this.partnerCities.remove(v);
        if (wasRemoved) {
            java.util.List<org.tbee.sway.City> pretendedOldValue = $swayWrap((java.util.List<org.tbee.sway.City>)new org.tbee.sway.beanGenerator.ListPretendingToHaveAddedItem<org.tbee.sway.City>(this.partnerCities, v));
            firePropertyChange("partnerCities", pretendedOldValue, getPartnerCities());
        }
    }

    // ---------------------
    // things
    public java.util.List getThings() {
        return $swayWrap(this.things);
    }
    private void setThings(java.util.List v) {
        fireVetoableChange("things", $swayWrap(this.things), $swayWrap(v));
        firePropertyChange("things", $swayWrap(this.things), $swayWrap(this.things = v));
    }
    public java.util.List things() {
        return getThings();
    }
    public org.tbee.sway.City things(java.util.List v) {
        setThings(v);
        return (org.tbee.sway.City)this;
    }
    public org.tbee.sway.binding.BindingEndpoint<java.util.List> things$() {
        return org.tbee.sway.binding.BindingEndpoint.of(this, "things");
    }
    static public org.tbee.sway.binding.BindingEndpoint<java.util.List> things$(org.tbee.sway.binding.BeanBinder<org.tbee.sway.City> beanBinder) {
        return org.tbee.sway.binding.BindingEndpoint.of(beanBinder, "things");
    }
    final static public String THINGS = "things";
    public void addThing(Object v) {
        var pretendedNewValue = $swayWrap(new org.tbee.sway.beanGenerator.ListPretendingToHaveAddedItem<Object>(this.things, v));
        fireVetoableChange("things", getThings(), pretendedNewValue);
        boolean wasAdded = this.things.add(v);
        if (wasAdded) {
            var pretendedOldValue = $swayWrap(new org.tbee.sway.beanGenerator.ListPretendingToHaveRemovedItem<Object>(this.things, v));
            firePropertyChange("things", pretendedOldValue, getThings());
        }
    }
    public void removeThing(Object v) {
        var pretendedNewValue = $swayWrap(new org.tbee.sway.beanGenerator.ListPretendingToHaveRemovedItem<Object>(this.things, v));
        fireVetoableChange("things", getThings(), pretendedNewValue);
        boolean wasRemoved = this.things.remove(v);
        if (wasRemoved) {
            var pretendedOldValue = $swayWrap(new org.tbee.sway.beanGenerator.ListPretendingToHaveAddedItem<Object>(this.things, v));
            firePropertyChange("things", pretendedOldValue, getThings());
        }
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(nameNot,distance,surfaceInKM2,growing,cityRights,sisterCity,partnerCities,things);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        org.tbee.sway.City other = (org.tbee.sway.City)obj;
        return java.util.Objects.equals(this.nameNot, other.nameNot)
            && java.util.Objects.equals(this.distance, other.distance)
            && java.util.Objects.equals(this.surfaceInKM2, other.surfaceInKM2)
            && java.util.Objects.equals(this.growing, other.growing)
            && java.util.Objects.equals(this.cityRights, other.cityRights)
            && java.util.Objects.equals(this.sisterCity, other.sisterCity)
            && java.util.Objects.equals(this.partnerCities, other.partnerCities)
            && java.util.Objects.equals(this.things, other.things);
    }

    @Override
    public String toString() {
        return super.toString()
             + ",nameNot=" + nameNot
             + ",distance=" + distance
             + ",surfaceInKM2=" + surfaceInKM2
             + ",growing=" + growing
             + ",cityRights=" + cityRights
             + ",partnerCities=" + partnerCities
             + ",things=" + things;
    }

   private <T> T $swayWrap(T object) {
       if (object instanceof java.util.List<?>) {
           return (T)java.util.Collections.unmodifiableList((java.util.List<?>)object);
       }
       return object;
   }

}

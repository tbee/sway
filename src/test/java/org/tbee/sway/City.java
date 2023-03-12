package org.tbee.sway;

import org.tbee.sway.binding.BeanBinder;
import org.tbee.sway.binding.BindingEndpoint;
import org.tbee.util.AbstractBean;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class City extends AbstractBean<City> {

    @Override
    public void firePropertyChange(String name, Object before, Object after) {
        System.out.println(this.getClass().getSimpleName() + "." + name + ": " + before + " -> " + after);
        super.firePropertyChange(name, before, after);
    }

    public City() {
    }

    public City(String name, int distance) {
        this.name = name;
        this.distance = distance;
    }

    /** name: string property */
    public void setName(String v) {
        fireVetoableChange(NAME, this.name, v);
        firePropertyChange(NAME, this.name, this.name = v);
    }
    public String getName() {
        return name;
    }
    private String name;
    static public String NAME = "name";
    public City name(String v) {
        setName(v);
        return this;
    }
    public BindingEndpoint<String> name$() {
        return BindingEndpoint.of(this, NAME);
    }
    static public BindingEndpoint<String> name$(BeanBinder<City> beanBinder) {
        return BindingEndpoint.of(beanBinder, NAME);
    }

    /** distance: integer property */
    public void setDistance(int v) {
        if (v < 0) {
            throw new IllegalArgumentException("Age must be >= 0");
        }
        fireVetoableChange(DISTANCE, this.distance, v);
        firePropertyChange(List.of(derived(ROUNDTRIP, getRoundTrip(), () -> getRoundTrip())) // TBEERNOT is there a better way to do this? Binding?
                         , DISTANCE, this.distance, this.distance = v);
    }
    public int getDistance() {
        return distance;
    }
    private int distance;
    static public String DISTANCE = "distance";
    public City distance(int v) {
        setDistance(v);
        return this;
    }
    public BindingEndpoint<Integer> distance$() {
        return BindingEndpoint.of(this, DISTANCE, null);
    }
    static public BindingEndpoint<Integer> distance$(BeanBinder<City> beanBinder) {
        return BindingEndpoint.of(beanBinder, DISTANCE);
    }

    /** distanceInt: a property without actual storage, but forwarding to age */
    public void setDistanceInt(Integer v) {
        setDistance(v);
    }
    public Integer getDistanceInt() {
        return getDistance();
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

    /** surface: BigDecimal property */
    public void setSurfaceInKM2(BigDecimal v) {
        fireVetoableChange(SURFACEINKM2, this.surfaceInKM2, v);
        firePropertyChange(SURFACEINKM2, this.surfaceInKM2, this.surfaceInKM2 = v);
    }
    public BigDecimal getSurfaceInKM2() {
        return surfaceInKM2;
    }
    private BigDecimal surfaceInKM2;
    static public String SURFACEINKM2 = "surfaceInKM2";
    public City surfaceInKM2(BigDecimal v) {
        this.setSurfaceInKM2(v);
        return this;
    }

    /** growing: boolean property */
    public void setGrowing(boolean v) {
        fireVetoableChange(GROWING, this.growing, v);
        firePropertyChange(GROWING, this.growing, this.growing = v);
    }
    public boolean getGrowing() {
        return growing;
    }
    private boolean growing = true;
    static public String GROWING = "growing";
    public City growing(boolean v) {
        setGrowing(v);
        return this;
    }

    /** cityRights: Boolean property */
    public void setCityRights(Boolean v) {
        fireVetoableChange(CITYRIGHTS, this.cityRights, v);
        firePropertyChange(CITYRIGHTS, this.cityRights, this.cityRights = v);
    }
    public Boolean getCityRights() {
        return cityRights;
    }
    private Boolean cityRights = null;
    static public String CITYRIGHTS = "cityRights";
    public City cityRights(Boolean v) {
        setCityRights(v);
        return this;
    }

    /** sisterCity: City property */
    public void setSisterCity(City v) {
        fireVetoableChange(SISTERCITY, this.sisterCity, v);
        firePropertyChange(SISTERCITY, this.sisterCity, this.sisterCity = v);
    }
    public City getSisterCity() {
        return sisterCity;
    }
    private City sisterCity;
    static public String SISTERCITY = "sisterCity";
    public City sisterCity(City v) {
        setSisterCity(v);
        return this;
    }

    @Override
    public String toString() {
        return super.toString() //
                + ",name=" + name
                + ",age=" + distance
                + ",surfaceInKM2=" + surfaceInKM2
                + ",growing=" + growing
                + ",cityRights=" + cityRights
                ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, distance, surfaceInKM2, sisterCity, growing, cityRights);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        City other = (City) obj;
        return Objects.equals(name, other.name) //
            && Objects.equals(distance, other.distance) //
            && Objects.equals(surfaceInKM2, other.surfaceInKM2) //
            && Objects.equals(sisterCity, other.sisterCity) //
            && Objects.equals(growing, other.growing) //
            && Objects.equals(cityRights, other.cityRights) //
        ;
    }
}

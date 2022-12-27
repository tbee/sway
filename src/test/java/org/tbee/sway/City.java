package org.tbee.sway;

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
                ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, distance, surfaceInKM2);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        City other = (City) obj;
        return Objects.equals(name, other.name) //
            && Objects.equals(distance, other.distance) //
            && Objects.equals(sisterCity, other.sisterCity) //
        ;
    }
}

package org.tbee.sway;

import org.tbee.util.AbstractBean;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class Bean1 extends AbstractBean<Bean1> {

    @Override
    public void firePropertyChange(String name, Object before, Object after) {
        System.out.println(this.getClass().getSimpleName() + "." + name + ": " + before + " -> " + after);
        super.firePropertyChange(name, before, after);
    }

    public Bean1() {
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
    public Bean1 name(String v) {
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
    public Bean1 distance(int v) {
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
    public Bean1 distanceInt(Integer v) {
        setDistanceInt(v);
        return this;
    }

    /** calc: a derived property */
    public Integer getRoundTrip() {
        return distance * 2;
    }
    static public String ROUNDTRIP = "roundTrip";

    /** length: BigDecimal property */
    public void setLength(BigDecimal v) {
        fireVetoableChange(LENGTH, this.length, v);
        firePropertyChange(LENGTH, this.length, this.length = v);
    }
    public BigDecimal getLength() {
        return length;
    }
    private BigDecimal length;
    static public String LENGTH = "length";
    public Bean1 length(BigDecimal v) {
        setLength(v);
        return this;
    }

    @Override
    public String toString() {
        return super.toString() //
                + ",name=" + name
                + ",age=" + distance
                + ",length=" + length
                ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, distance);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Bean1 other = (Bean1) obj;
        return Objects.equals(name, other.name) //
            && Objects.equals(distance, other.distance) //
            && Objects.equals(length, other.length) //
        ;
    }
}

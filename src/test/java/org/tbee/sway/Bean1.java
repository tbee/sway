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

    /** age: integer property */
    public void setAge(int v) {
        fireVetoableChange(AGE, this.age, v);
        firePropertyChange(List.of(derived(CALC, getCalc(), () -> getCalc())) // TBEERNOT is there a better way to do this? Binding?
                , AGE, this.age, this.age = v);
    }
    public int getAge() {
        return age;
    }
    private int age;
    static public String AGE = "age";
    public Bean1 age(int v) {
        setAge(v);
        return this;
    }

    /** ageInt: a property without actual storage, but forwarding to age */
    public void setAgeInt(Integer v) {
        setAge(v);
    }
    public Integer getAgeInt() {
        return getAge();
    }
    static public String AGEINT = "ageInt";
    public Bean1 ageInt(Integer v) {
        setAgeInt(v);
        return this;
    }

    /** calc: a derived property */
    public Integer getCalc() {
        return age * 2;
    }
    static public String CALC = "calc";

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
                + ",age=" + age
                + ",length=" + length
                ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Bean1 other = (Bean1) obj;
        return Objects.equals(name, other.name) //
            && Objects.equals(age, other.age) //
        ;
    }
}

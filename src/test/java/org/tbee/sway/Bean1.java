package org.tbee.sway;

import org.tbee.sway.support.AbstractBean;

import java.util.List;
import java.util.Objects;

public class Bean1 extends AbstractBean<Bean1> {

    @Override
    public void firePropertyChange(String name, Object before, Object after) {
        System.out.println(this.getClass().getSimpleName() + "." + name + ": " + before + " -> " + after);
        super.firePropertyChange(name, before, after);
    }

    public Bean1(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public void setName(String v) {
        fireVetoableChange(NAME, this.name, v);
        firePropertyChange(NAME, this.name, this.name = v);
    }
    public String getName() {
        return name;
    }
    String name;
    static public String NAME = "name";

    public void setAge(int v) {
        fireVetoableChange(AGE, this.age, v);
        firePropertyChange(List.of(cascade(CALC, getCalc(), () -> getCalc())) //
                , AGE, this.age, this.age = v);
    }
    public int getAge() {
        return age;
    }
    int age;
    static public String AGE = "age";

    public void setAgeInt(Integer age) {
        setAge(age);
    }
    public Integer getAgeInt() {
        return getAge();
    }
    static public String AGEINT = "ageInt";

    public Integer getCalc() {
        return age * 2;
    }
    static public String CALC = "calc";

    @Override
    public String toString() {
        return super.toString() //
                + ",name=" + name
                + ",age=" + age
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

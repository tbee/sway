package org.tbee.sway;

public class Bean1 extends AbstractBean<Bean1> {
    String name;
    int age;

    public Bean1(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        System.out.println("setName " + name);
        this.name = name;
    }

    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        System.out.println("setAge " + age);
        this.age = age;
    }

    public Integer getAgeInt() {
        return getAge();
    }
    public void setAgeInt(Integer age) {
        setAge(age);
    }

    public Integer getCalc() {
        return age * 2;
    }
}

package org.tbee.sway;

public class Person {

    String name;
    public String getName() {
        return this.name;
    }
    public void setName(String v) {
        this.name = v;
    }

    @Override
    public int hashCode() {
        // return super.hashCode();
        return java.util.Objects.hash(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Person other = (Person)obj;
        return java.util.Objects.equals(this.name, other.name);
    }

    @Override
    public String toString() {
        return super.toString() + "#" + System.identityHashCode(this)
                + ",name=" + name;
    }
}

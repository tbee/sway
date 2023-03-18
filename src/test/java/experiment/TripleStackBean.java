package experiment;

abstract public class TripleStackBean extends TripleStackData { // This class is generated
    public String name() {
        return name;
    }

    public void setName(String name) {
        firePropertyChange("name", this.name, this.name = name);
    }
}

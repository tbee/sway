package experiment;

public class DoubleStack extends DoubleStackData { // This class is generated
    public String name() {
        return name;
    }

    public void setName(String name) {
        firePropertyChange("name", this.name, this.name = name);
    }
}

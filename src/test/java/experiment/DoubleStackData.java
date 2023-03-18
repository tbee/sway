package experiment;

import org.tbee.util.AbstractBean;

abstract public class DoubleStackData extends AbstractBean<DoubleStack> {
    private final DoubleStack self = (DoubleStack)this;
    String name;

    public void custom() {
        self.setName("test");
    }

}

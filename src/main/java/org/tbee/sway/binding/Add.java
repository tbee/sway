package org.tbee.sway.binding;

public class Add implements BindChainNode<Integer, Integer> {

    private final int value;
    private Add(int value) {
        this.value = value;
    }

    @Override
    public Integer binderToBindee(Integer in) {
        if (in == null) {
            return null;
        }
        return in + this.value;
    }

    @Override
    public Integer bindeeToBinder(Integer in) {
        if (in == null) {
            return null;
        }
        return in - this.value;
    }

    static public Add of(int value) {
        return new Add(value);
    }
}

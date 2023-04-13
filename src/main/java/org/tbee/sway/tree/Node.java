package org.tbee.sway.tree;

public class Node {
    final public Object value;

    public Node(Object value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(value);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof Node)) {
            return false;
        }
        Node other = (Node) o;

        return System.identityHashCode(other.value) == System.identityHashCode(value);
    }

    @Override
    public String toString() {
        return "Node->" + value;
    }
}

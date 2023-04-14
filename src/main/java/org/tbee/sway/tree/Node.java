package org.tbee.sway.tree;

import java.lang.ref.WeakReference;
import java.util.Map;

public class Node {
    final private WeakReference<Object> value;
    final private WeakReference<Object> parent;
    final private Map<Object, Node> valueToNode;

    public Node(Object value, Object parent, Map<Object, Node> valueToNode) {
        this.value = new WeakReference<>(value);
        this.parent = new WeakReference<>(parent);
        this.valueToNode = valueToNode;
    }

    public Object value() {
        Object v = value.get();
        if (v == null) {
            valueToNode.remove(this);
        }
        return v;
    }
    public Object parent() {
        return parent.get();
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(value.get());
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

        return System.identityHashCode(other.value.get()) == System.identityHashCode(value.get());
    }

    @Override
    public String toString() {
        return "Node->" + value.get();
    }
}

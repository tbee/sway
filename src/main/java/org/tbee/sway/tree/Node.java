package org.tbee.sway.tree;

import java.lang.ref.WeakReference;
import java.util.Map;

public class Node {

    static final private Object NULL = new Object();

    final private WeakReference<Object> value;
    final private WeakReference<Object> parent;
    final private Map<Object, Node> valueToNode;

    public Node(Object value, Object parent, Map<Object, Node> valueToNode) {
        this.value = new WeakReference<>(value == null ? NULL : value);
        this.parent = new WeakReference<>(parent == null ? NULL : parent);
        this.valueToNode = valueToNode;
    }

    public Object value() {
        Object v = value.get();
        if (v == null) {
            // this all happens in the EDT, so no locking necessary
            valueToNode.remove(this);
        }
        return v == NULL ? null : v;
    }
    public Object parent() {
        Object v = parent.get();
        if (v == null) {
            // this all happens in the EDT, so no locking necessary
            valueToNode.remove(this);
        }
        return v == NULL ? null : v;
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

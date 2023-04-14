package org.tbee.sway.tree;

import org.tbee.sway.STree;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class STreeModel<T> implements TreeModel {

    private Map<T, Node> valueToNode = new TreeMap<>((o1, o2) -> {
        int h1 = o1 == null ? 0 : System.identityHashCode(o1);
        int h2 = o2 == null ? 0 : System.identityHashCode(o2);
        return Integer.compare(h1, h2);
    });

    public Node node(T value, T parent) {
        Node node = new Node(value, parent, (Map<Object, Node>) valueToNode); // TBEERNOT caching?
        if (value != null) {
            valueToNode.put(value, node);
        }
        return node;
    }
    public Node findNode(T value) {
        return valueToNode.get(value);
    }

    private final STree<T> sTree;
    protected List<TreeModelListener> treeModelListeners = new ArrayList<>();

    public STreeModel(STree<T> sTree) {
        this.sTree = sTree;
    }

    @Override
    public Object getRoot() {
        return node(sTree.getRoot(), null);
    }

    private List<Node> getChildren(Object parent) {
        Node parentNode = (Node)parent;
        T value = (T)parentNode.value();
        List<Node> children = sTree.determineChildrenOf(value).stream()
                .map(child -> node(child, value))
                .toList(); // TBEERNOT caching?
        return children;
    }

    @Override
    public Object getChild(Object parent, int index) {
        return getChildren(parent).get(index);
    }

    @Override
    public int getChildCount(Object parent) {
        return getChildren(parent).size();
    }

    @Override
    public boolean isLeaf(Object node) {
        return getChildren(node).isEmpty();
    }

    @Override
    public void valueForPathChanged(TreePath treePath, Object newValue) {
        treeNodesChanged(new TreeModelEvent(this, treePath));
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        return getChildren(parent).indexOf(child);
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        treeModelListeners.add(l);
    }
    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        treeModelListeners.remove(l);
    }

    public void treeStructureChanged(TreeModelEvent e) {
        treeModelListeners.forEach(tml -> tml.treeStructureChanged(e));
    }
    public void treeStructureChanged(TreePath treePath) {
        treeStructureChanged(new TreeModelEvent(this, treePath));
    }
    public void treeNodesInserted(TreeModelEvent e) {
        treeModelListeners.forEach(tml -> tml.treeNodesInserted(e));
    }
    public void treeNodesInserted(TreePath treePath) {
        treeNodesInserted(new TreeModelEvent(this, treePath));
    }
    public void treeNodesRemoved(TreeModelEvent e) {
        treeModelListeners.forEach(tml -> tml.treeNodesRemoved(e));
    }
    public void treeNodesRemoved(TreePath treePath) {
        treeNodesRemoved(new TreeModelEvent(this, treePath));
    }
    public void treeNodesChanged(TreeModelEvent e) {
        treeModelListeners.forEach(tml -> tml.treeNodesChanged(e));
    }
    public void treeNodesChanged(TreePath treePath) {
        treeNodesChanged(new TreeModelEvent(this, treePath));
    }
}

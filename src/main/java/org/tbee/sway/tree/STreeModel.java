package org.tbee.sway.tree;

import org.tbee.sway.STree;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;

public class STreeModel<T> implements TreeModel {

    public Node node(T value) {
        return new Node(value); // TBEERNOT caching?
    }

    private final STree<T> sTree;
    protected List<TreeModelListener> treeModelListeners = new ArrayList<>();

    public STreeModel(STree<T> sTree) {
        this.sTree = sTree;
    }

    @Override
    public Object getRoot() {
        return node(sTree.getRoot());
    }

    private List<Node> getChildren(Object parent) {
        Node parentNode = (Node)parent;
        T value = (T)parentNode.value;
        List<Node> children = sTree.determineChildrenOf(value).stream()
                .map(child -> node(child))
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

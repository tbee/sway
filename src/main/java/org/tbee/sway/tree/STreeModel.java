package org.tbee.sway.tree;

import org.tbee.sway.STree;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;

public class STreeModel<T> implements TreeModel {

    private final STree<T> sTree;
    protected List<TreeModelListener> treeModelListeners = new ArrayList<>();

    public STreeModel(STree<T> sTree) {
        this.sTree = sTree;
    }

    @Override
    public Object getRoot() {
        System.out.println("!!!!! getRoot " + sTree.getRoot());
        return sTree.getRoot();
    }

    private List<T> getChildren(Object parent) {
        T treeNode = (T)parent;
        List<T> children = sTree.getChildren().apply(treeNode);
        System.out.println("!!!!! getChildren " + parent + " -> " + children);
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
    public void valueForPathChanged(TreePath path, Object newValue) {

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
    protected void treeStructureChanged(TreeModelEvent e) {
        treeModelListeners.forEach(tml -> tml.treeStructureChanged(e));
    }
    protected void treeNodesInserted(TreeModelEvent e) {
        treeModelListeners.forEach(tml -> tml.treeNodesInserted(e));
    }
    protected void treeNodesRemoved(TreeModelEvent e) {
        treeModelListeners.forEach(tml -> tml.treeNodesRemoved(e));
    }
    protected void treeNodesChanged(TreeModelEvent e) {
        treeModelListeners.forEach(tml -> tml.treeNodesChanged(e));
    }
}

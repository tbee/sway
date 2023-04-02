package org.tbee.sway.tree;

import org.tbee.sway.STree;

import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreePath;

public class STreeCore<T> extends JTree {

    private final STree<T> sTree;

    public STreeCore(STree<T> sTree) {
        super(new STreeModel<T>(sTree));
        this.sTree = sTree;
    }

    public STreeModel<T> getSTreeModel() {
        return (STreeModel<T>) getModel();
    }

    public void treeStructureChanged() {
        getSTreeModel().treeStructureChanged(new TreeModelEvent(sTree.getRoot(), new Object[]{sTree.getRoot()}));
    }
    public void treeStructureChanged(TreePath treePath) {
        getSTreeModel().treeStructureChanged(new TreeModelEvent(sTree.getRoot(), treePath));
    }
}
package org.tbee.sway.tree;

import org.tbee.sway.STree;

import javax.swing.JTree;

public class STreeCore<T> extends JTree {

    private final STree<T> sTree;

    public STreeCore(STree<T> sTree) {
        super(new STreeModel<T>(sTree));
        this.sTree = sTree;
    }

    public STreeModel<T> getSTreeModel() {
        return (STreeModel<T>) getModel();
    }
}
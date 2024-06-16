package org.tbee.sway.list;

import org.tbee.sway.SList;

public class ListModel<T> extends javax.swing.AbstractListModel<T> {

    final private SList<T> sList;

    public ListModel(SList<T> sList) {
        this.sList = sList;
    }

    @Override
    public int getSize() {
        return sList.getItems().size();
    }

    @Override
    public T getElementAt(int index) {
        return sList.getItems().get(index);
    }

    public void contentsChanged() {
        fireContentsChanged(this, 0, getSize());
    }
}

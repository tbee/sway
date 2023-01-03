package org.tbee.sway.list;

import java.util.Collections;
import java.util.List;

public class ListModel<T> extends javax.swing.AbstractListModel<T> {

    @Override
    public int getSize() {
        return data.size();
    }

    @Override
    public T getElementAt(int index) {
        return data.get(index);
    }


    // =======================================================================
    // DATA

    private List<T> data = List.of();

    /**
     *
     * @param v
     */
    public void setData(List<T> v) {
//        unregisterFromAllBeans();
        this.data = Collections.unmodifiableList(v); // We don't allow outside changes to the provided list
//        registerToAllBeans();
    }
    public List<T> getData() {
        return this.data;
    }
}

package org.tbee.sway.list;

import javax.swing.JList;
import java.util.Collections;
import java.util.List;

public class SListCore<T> extends JList<T> {

    private final ListModel<T> listModel = new ListModel<>();

    public SListCore() {
        super();
        setModel(listModel);
    }

    public ListModel<T> getListModel() {
        return listModel;
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
    public SListCore<T> data(List<T> v) {
        setData(v);
        return this;
    }


}

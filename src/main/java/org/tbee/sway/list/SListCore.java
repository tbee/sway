package org.tbee.sway.list;

import org.tbee.sway.SList;

import javax.swing.JList;

public class SListCore<T> extends JList<T> {

    final private SList<T> sList;
    private final ListModel<T> listModel;

    public SListCore(SList<T> sList) {
        super();
        this.sList = sList;
        this.listModel = new ListModel<>(sList);
        setModel(listModel);
    }

    public ListModel<T> getListModel() {
        return listModel;
    }
}

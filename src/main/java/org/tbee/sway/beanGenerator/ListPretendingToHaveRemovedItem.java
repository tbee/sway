package org.tbee.sway.beanGenerator;

import java.util.AbstractList;
import java.util.List;

public class ListPretendingToHaveRemovedItem<T> extends AbstractList<T> {

    final private List<T> list;
    final private int supposedlyRemovedIndex;

    /**
     * @param list
     * @param supposedlyRemovedIndex
     */
    public ListPretendingToHaveRemovedItem(List<T> list, int supposedlyRemovedIndex) {
        this.list = list;
        this.supposedlyRemovedIndex = supposedlyRemovedIndex;
    }

    /**
     *
     * @param list
     * @param supposedlyRemovedItem
     */
    public ListPretendingToHaveRemovedItem(List<T> list, T supposedlyRemovedItem) {
        this(list, list.indexOf(supposedlyRemovedItem));
    }

    public int size() {
        return list.size() - 1;
    }

    public T get(int index) {
        if (index < this.supposedlyRemovedIndex) return this.list.get(index);
        return this.list.get(index + 1);
    }
}

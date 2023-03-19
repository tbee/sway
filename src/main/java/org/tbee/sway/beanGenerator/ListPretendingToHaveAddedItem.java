package org.tbee.sway.beanGenerator;

import java.util.AbstractList;
import java.util.List;

public class ListPretendingToHaveAddedItem<T>  extends AbstractList<T> {

    final private List<T> list;
    final private int supposedlyAddedIndex;
    final private T supposedlyAddedItem;

    /**
     *
     * @param list
     * @param supposedlyAddedItem
     */
    public ListPretendingToHaveAddedItem(List<T> list, T supposedlyAddedItem)
    {
        this(list, list.size(), supposedlyAddedItem);
    }

    /**
     *
     * @param list
     * @param supposedlyAddedIndex
     * @param supposedlyAddedItem
     */
    public ListPretendingToHaveAddedItem(List<T> list, int supposedlyAddedIndex, T supposedlyAddedItem) {
        this.list = list;
        this.supposedlyAddedIndex = supposedlyAddedIndex;
        this.supposedlyAddedItem = supposedlyAddedItem;
    }

    public int size() {
        return list.size() + 1;
    }

    public T get(int index) {
        if (index == this.supposedlyAddedIndex) return this.supposedlyAddedItem;
        if (index < this.supposedlyAddedIndex) return this.list.get(index);
        return this.list.get(index - 1);
    }
}

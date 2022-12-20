package org.tbee.util;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.*;

/**
 * An Arraylist storing its contents using SoftReferences.
 * A dead reference returns null, therefore storing null as an explicit value is not allowed.
 * The user should call "garbageCollect" on a somewhat regular basis to make sure the dead wood gets cleaned out.
 * However iterator(), removeAll() and retainAll() also do a garbageCollect prior or after the operation.
 */
public class SoftArrayList<T> implements List<T> {

    private List<SoftReference<T>> list = new ArrayList<SoftReference<T>>();

    public void add(int index, T element) {
        if (element == null) throw new IllegalArgumentException("Null not allowed in a SoftArrayList");
        list.add(index, new SoftReference<T>(element));
    }

    public boolean add(T o) {
        if (o == null) throw new IllegalArgumentException("Null not allowed in a SoftArrayList");
        return list.add(new SoftReference<T>(o));
    }

    public boolean addAll(Collection<? extends T> c) {
        Iterator<? extends T> i = c.iterator();
        while (i.hasNext()) add(i.next());
        return c.size() > 0;
    }

    public boolean addAll(int index, Collection<? extends T> c) {
        Iterator<? extends T> i = c.iterator();
        while (i.hasNext()) {
            add(index, i.next());
            index++;
        }
        return c.size() > 0;
    }

    public void clear() {
        list.clear();
    }

    public boolean contains(Object o) {
        for (Reference<? extends T> a : list) {
            if (Objects.equals(o, a.get())) return true;
        }
        return false;
    }

    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) return false;
        }
        return true;
    }

    public boolean equals(Object o) {
        // equals uses iterators to scan boths lists, our iterator already hides the reference
        return list.equals(o);
    }

    public T get(int index) {
        Reference<? extends T> r = (Reference<? extends T>) list.get(index);
        T lValue = r.get();
        if (lValue == null) list.remove(index);
        return lValue;
    }

    public int hashCode() {
        return list.hashCode();
    }

    public int indexOf(Object o) {
        int n = list.size();
        for (int i = 0; i < n; i++) {
            if (Objects.equals(o, list.get(i).get())) return i;
        }
        return -1;
    }

    public int lastIndexOf(Object o) {
        int n = list.size();
        for (int i = n - 1; i >= 0; i--) {
            if (Objects.equals(o, list.get(i).get())) return i;
        }
        return -1;
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public Iterator<T> iterator() {
        garbageCollect();
        final Iterator<SoftReference<T>> ii = list.iterator();
        return new Iterator<T>() {
            public boolean hasNext() {
                return ii.hasNext();
            }

            public T next() {
                return ii.next().get();
            }

            public void remove() {
                ii.remove();
            }
        };
    }

    public ListIterator<T> listIterator() {
        return listIterator(0);
    }

    public ListIterator<T> listIterator(int index) {
        garbageCollect();
        final ListIterator<SoftReference<T>> ii = list.listIterator(index);
        return new ListIterator<T>() {
            public boolean hasNext() {
                return ii.hasNext();
            }

            public T next() {
                return ii.next().get();
            }

            public void remove() {
                ii.remove();
            }

            public void add(T o) {
                ii.add(new SoftReference<T>(o));
            }

            public boolean hasPrevious() {
                return ii.hasPrevious();
            }

            public int nextIndex() {
                return ii.nextIndex();
            }

            public T previous() {
                Reference<T> r = ii.previous();
                return r.get();
            }

            public int previousIndex() {
                return ii.previousIndex();
            }

            public void set(T o) {
                ii.set(new SoftReference<T>(o));
            }
        };
    }

    public T remove(int index) {
        Reference<? extends T> n = (Reference<? extends T>) list.remove(index);
        if (n == null) return null;
        return n.get();
    }

    public boolean remove(Object o) {
        int n = list.size();
        for (int i = 0; i < n; i++) {
            Reference<? extends T> a = (Reference<? extends T>) list.get(i);
            if (a.get() == o) list.remove(i);
            return true;
        }
        return false;
    }

    public boolean removeAll(Collection<?> c) {
        boolean b = false;
        for (Object o : c) {
            b |= list.remove(o);
        }
        garbageCollect();
        return b;
    }

    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        Iterator<T> e = iterator();
        while (e.hasNext()) {
            if (!c.contains(e.next())) {
                e.remove();
                modified = true;
            }
        }
        return modified;
    }

    public T set(int index, T element) {
        if (element == null) throw new IllegalArgumentException("Null not allowed in a SoftArrayList");
        return ((Reference<? extends T>) list.set(index, new SoftReference<T>(element))).get();
    }

    public int size() {
        return list.size();
    }

    public List subList(int fromIndex, int toIndex) {
        SoftArrayList<T> nn = new SoftArrayList<T>();
        nn.list = this.list.subList(fromIndex, toIndex);
        return nn;
    }

    public Object[] toArray() {
        return list.toArray();
    }

    public <T> T[] toArray(T[] a2) {
        garbageCollect();
        Object[] r = new Object[list.size()];
        int n = list.size();
        for (int i = 0; i < n; i++) {
            Reference<? extends T> a = (Reference<? extends T>) list.get(i);
            r[i] = a.get();
        }
        return (T[]) r;
    }

    public void garbageCollect() {
        Iterator i = list.iterator();
        while (i.hasNext()) {
            Reference nn = (Reference) i.next();
            if (nn == null || nn.get() == null) i.remove();
        }
    }
}

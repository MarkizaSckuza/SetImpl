package com.margo.set.mapset;

import java.util.*;

public class HashSet<E> implements Set<E> {

    private Map<E, Object> elements;

    public HashSet() {
        elements = new HashMap<E, Object>();
    }

    public HashSet(int initialCapacity) {
        elements = new HashMap<E, Object>(initialCapacity);
    }

    public HashSet(Collection<E> collection) {
        this();
        addAll(collection);
    }

    public int size() {
        return elements.size();
    }

    public boolean isEmpty() {
        return elements.isEmpty();
    }

    public boolean contains(Object o) {
        Iterator<E> it = iterator();
        if (o == null) {
            while (it.hasNext())
                if (it.next() == null)
                    return true;
        } else {
            while (it.hasNext())
                if (o.equals(it.next()))
                    return true;
        }
        return false;
    }

    public Iterator<E> iterator() {
        return elements.keySet().iterator();
    }

    public Object[] toArray() {
        return elements.keySet().toArray();
    }

    public <T> T[] toArray(T[] a) {
        return elements.keySet().toArray(a);
    }

    public boolean add(E e) {
        return elements.put(e, null) == null;
    }

    public boolean remove(Object o) {
        return elements.remove(o) == null;
    }

    public boolean containsAll(Collection<?> c) {
        for (Object e : c)
            if (!contains(e))
                return false;
        return true;
    }

    public boolean addAll(Collection<? extends E> c) {
        if (containsAll(c)) {
            return false;
        }
        for (E e : c) {
            add(e);
        }
        return true;
    }

    public boolean retainAll(Collection<?> c) {
        return elements.keySet().retainAll(c);
    }

    public boolean removeAll(Collection<?> c) {
        return elements.keySet().removeAll(c);
    }

    public void clear() {
        elements.clear();
    }

    public boolean equals(Object o) {
        if (o == this)
            return true;

        if (!(o instanceof Set))
            return false;
        Collection<?> c = (Collection<?>) o;
        if (c.size() != size())
            return false;
        try {
            return containsAll(c);
        } catch (ClassCastException unused)   {
            return false;
        } catch (NullPointerException unused) {
            return false;
        }
    }

    public int hashCode() {
        int h = 0;
        Iterator<E> i = iterator();
        while (i.hasNext()) {
            E obj = i.next();
            if (obj != null)
                h += obj.hashCode();
        }
        return h;
    }
}

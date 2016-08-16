package com.margo.set.arrayset;

import java.util.*;

public class HashSet<E> implements Set<E> {

    private static final int INITIAL_CAPACITY = 10;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private Bucket<E>[] elements;
    private int size;
    private float loadFactor;
    private int threshold;
    private int modCount;

    public HashSet() {
        elements = new Bucket[INITIAL_CAPACITY];
        size = 0;
        loadFactor = DEFAULT_LOAD_FACTOR;
        threshold = (int) (INITIAL_CAPACITY * loadFactor);
        modCount = 0;
    }

    public HashSet(int initialCapacity) {
        elements = new Bucket[initialCapacity];
        size = 0;
        loadFactor = DEFAULT_LOAD_FACTOR;
        threshold = (int) (initialCapacity * loadFactor);
        modCount = 0;
    }

    public HashSet(Collection<E> collection) {
        this(2 * collection.size());
        addAll(collection);
    }

    private static int hash(int h) {
        h ^= (h >>> 20) ^ (h >>> 12);
        return h ^ (h >>> 7) ^ (h >>> 4);
    }

    private static int indexFor(int h, int length) {
        return h & (length - 1);
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean contains(Object o) {

        int hashCode = o.hashCode();
        int hash = hash(hashCode);
        int i = indexFor(hash, elements.length);
        Bucket<E> bucket = elements[i];

        if (bucket == null || bucket.entries.isEmpty()) {
            return false;
        }
        for (Node<E> node : bucket.entries) {
            if (node.value.equals(o)) {
                return true;
            }
        }
        return false;
    }

    public Iterator iterator() {
        return new Itr();
    }

    public Object[] toArray() {

        if (isEmpty()) {
            return new Object[0];
        }

        Object[] array = new Object[size];

        int j = 0;
        for (Bucket<E> element : elements) {
            if (element != null && !element.entries.isEmpty()) {
                for (Node<E> node : element.entries) {
                    array[j] = node;
                    j++;
                }
            }
        }

        return array;
    }

    public boolean add(Object o) {

        if (contains(o)) {
            return false;
        }

        if (size++ >= threshold) {
            resize();
        }

        int hashCode = o.hashCode();
        int hash = hash(hashCode);
        int i = indexFor(hash, elements.length);
        Bucket<E> bucket = elements[i];

        if (bucket != null) {
            bucket.entries.add(new Node<E>(hashCode, (E) o));
        } else {
            elements[i] = new Bucket<E>(hash);
            elements[i].entries.add(new Node<E>(hashCode, (E) o));
        }
        modCount++;
        return true;
    }

    public boolean remove(Object o) {

        int hashCode = o.hashCode();
        int hash = hash(hashCode);
        int i = indexFor(hash, elements.length);
        Bucket<E> bucket = elements[i];

        if (bucket == null || !contains(o)) {
            return false;
        }
        for (Node<E> node : bucket.entries) {
            if (node.value.equals(o)) {
                bucket.entries.remove(node);
                size--;
            }
        }
        modCount++;
        return true;
    }

    public boolean addAll(Collection<? extends E> c) {

        int newSize = size + c.size();
        if (newSize >= threshold) {
            resize(newSize);
        }

        if (containsAll(c)) {
            return false;
        }

        for (E element : c) {
            add(element);
        }
        return true;
    }

    public void clear() {
        if (elements != null && size > 0) {
            size = 0;
            for (int i = 0; i < elements.length; i++) {
                elements[i] = null;
            }
        }
        modCount++;
    }

    public boolean removeAll(Collection<?> c) {
        if (!containsAll(c)) {
            return false;
        }

        for (Bucket<E> bucket : elements) {
            if (bucket != null && !bucket.entries.isEmpty()) {
                for (Node<E> node : bucket.entries) {
                    if (c.contains(node.value)) {
                        remove(node.value);
                    }
                }
            }
        }
        return true;
    }

    public boolean retainAll(Collection<?> c) {
        for (Bucket<E> bucket : elements) {
            if (bucket != null && !bucket.entries.isEmpty()) {
                for (Node<E> node : bucket.entries) {
                    if (!c.contains(node.value)) {
                        remove(node);
                    }
                }

            }
        }
        return false;
    }

    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }

    public <T> T[] toArray(T[] a) {

        if (a.length < size) {
            return (T[]) Arrays.copyOf(toArray(), size, a.getClass());
        }
        System.arraycopy(toArray(), 0, a, 0, size);
        if (a.length > size)
            a[size] = null;
        return a;
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

    private void resize() {
        Bucket<E>[] oldElements = elements;

        int newCapacity = elements.length * 2;
        threshold = (int) (newCapacity * loadFactor);

        elements = new Bucket[newCapacity];
        transfer(oldElements);
    }

    private void resize(int neededSize) {
        Bucket<E>[] oldElements = elements;

        int newCapacity = neededSize * 2;
        threshold = (int) (newCapacity * loadFactor);
        size = 0;
        elements = new Bucket[newCapacity];
        transfer(oldElements);
    }

    private void transfer(Bucket<E>[] oldElements) {
        for (Bucket<E> bucket : oldElements) {
            if (bucket != null) {
                for (Node<E> node : bucket.entries) {
                    add(node.value);
                }
            }
        }
        modCount++;
    }

    private class Bucket<E> {

        private final int hash;
        private List<Node<E>> entries;

        private Bucket(int hash) {
            this.hash = hash;
            this.entries = new LinkedList<Node<E>>();
        }
    }

    private class Node<E> {

        private int hashCode;
        private E value;

        private Node(int hashCode, E value) {
            this.hashCode = hashCode;
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Node<?> node = (Node<?>) o;

            if (hashCode != node.hashCode) return false;
            return value != null ? value.equals(node.value) : node.value == null;
        }
    }

    private class Itr implements Iterator<E> {
        private int cursor;
        private int currentBucketIndex;
        private int currentNode;
        private int expectedModCount = modCount;

        public boolean hasNext() {
            return cursor != size;
        }

        public E next() {
            checkForModification();
            int i = cursor;
            if (i >= size)
                throw new NoSuchElementException();
            Bucket<E>[] elements = HashSet.this.elements;

            if (i >= elements.length) {
                throw new ConcurrentModificationException();
            }

            if (elements[currentBucketIndex] == null || elements[currentBucketIndex].entries.isEmpty()) {
                currentBucketIndex++;
                return next();
            }

            E value = elements[currentBucketIndex].entries.get(currentNode).value;

            if(elements[currentBucketIndex].entries.size() - 1 == currentNode) {
                currentNode = 0;
                currentBucketIndex++;
            } else {
                currentNode++;
            }
            cursor++;

            return value;
        }

        private void checkForModification() {
            if (expectedModCount != modCount) {
                throw new ConcurrentModificationException();
            }
        }
    }
}

package com.margo.set.arrayset;

import java.util.*;

public class HashSet<E> implements Set<E> {

    private static final int INITIAL_CAPACITY = 10;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private Bucket<E>[] buckets;
    private int size;
    private float loadFactor;
    private int threshold;
    private int modCount;

    public HashSet() {
        buckets = new Bucket[INITIAL_CAPACITY];
        size = 0;
        loadFactor = DEFAULT_LOAD_FACTOR;
        threshold = (int) (INITIAL_CAPACITY * loadFactor);
        modCount = 0;
    }

    public HashSet(int initialCapacity) {
        buckets = new Bucket[initialCapacity];
        size = 0;
        loadFactor = DEFAULT_LOAD_FACTOR;
        threshold = (int) (initialCapacity * loadFactor);
        modCount = 0;
    }

    public HashSet(Collection<E> collection) {
        this(2 * collection.size());
        addAll(collection);
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
        int i = indexFor(hash, buckets.length);
        Bucket<E> bucket = buckets[i];

        if (bucket == null || bucket.elements.isEmpty()) {
            return false;
        }
        return bucket.elements.stream().anyMatch(node -> node.value.equals(o));
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
        for (Bucket<E> element : buckets) {
            if (element != null && !element.elements.isEmpty()) {
                for (Node node : element.elements) {
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
        int i = indexFor(hash, buckets.length);
        Bucket<E> bucket = buckets[i];

        if (bucket != null) {
            bucket.elements.add(new Node(hashCode, (E) o));
        } else {
            buckets[i] = new Bucket<E>(hash);
            buckets[i].elements.add(new Node(hashCode, (E) o));
        }
        modCount++;
        return true;
    }

    public boolean remove(Object o) {

        int hashCode = o.hashCode();
        int hash = hash(hashCode);
        int i = indexFor(hash, buckets.length);
        Bucket<E> bucket = buckets[i];

        if (bucket == null || !contains(o)) {
            return false;
        }
        for (Node node : bucket.elements) {
            if (node.value.equals(o)) {
                bucket.elements.remove(node);
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
        if (buckets != null && size > 0) {
            size = 0;
            for (int i = 0; i < buckets.length; i++) {
                buckets[i] = null;
            }
        }
        modCount++;
    }

    public boolean removeAll(Collection<?> c) {
        if (!containsAll(c)) {
            return false;
        }
        return batchRemove(c, true);
    }

    public boolean retainAll(Collection<?> c) {
        return batchRemove(c, false);
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
        } catch (ClassCastException unused) {
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

    private int hash(int h) {
        h ^= (h >>> 20) ^ (h >>> 12);
        return h ^ (h >>> 7) ^ (h >>> 4);
    }

    private int indexFor(int h, int length) {
        return h & (length - 1);
    }

    private boolean batchRemove(Collection<?> c, boolean complement) {
        boolean modified = false;

        for (Bucket<E> bucket : buckets) {
            if (bucket != null && !bucket.elements.isEmpty()) {
                for (Node node : bucket.elements) {
                    if (c.contains(node.value) == complement) {
                        modified = remove(node.value);
                    }
                }
            }
        }
        return modified;
    }

    private void resize() {
        Bucket<E>[] oldElements = buckets;

        int newCapacity = buckets.length * 2;
        threshold = (int) (newCapacity * loadFactor);

        buckets = new Bucket[newCapacity];
        transfer(oldElements);
    }

    private void resize(int neededSize) {
        Bucket<E>[] oldBuckets = buckets;

        int newCapacity = neededSize * 2;
        threshold = (int) (newCapacity * loadFactor);
        size = 0;
        buckets = new Bucket[newCapacity];
        transfer(oldBuckets);
    }

    private void transfer(Bucket<E>[] oldElements) {
        for (Bucket<E> bucket : oldElements) {
            if (bucket != null) {
                for (Node node : bucket.elements) {
                    add(node.value);
                }
            }
        }
        modCount++;
    }

    private class Bucket<E> {

        private final int hash;
        private List<Node> elements;

        private Bucket(int hash) {
            this.hash = hash;
            this.elements = new LinkedList<Node>();
        }
    }

    private class Node {

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

            Node node = (Node) o;

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
            Bucket<E>[] buckets = HashSet.this.buckets;

            if (i >= buckets.length) {
                throw new ConcurrentModificationException();
            }

            if (buckets[currentBucketIndex] == null || buckets[currentBucketIndex].elements.isEmpty()) {
                currentBucketIndex++;
                return next();
            }

            E value = buckets[currentBucketIndex].elements.get(currentNode).value;

            if (buckets[currentBucketIndex].elements.size() - 1 == currentNode) {
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

package my;

import java.util.*;

public class CustomHashMap<K, V> implements Map<K, V> {

    private static final int CAPACITY = 10;
    private static double LOAD_FACTOR = 1.75;
    private int size;
    private Entry<K, V>[] table;
    transient Set<Map.Entry<K, V>> entrySet;


    public CustomHashMap() {
        this.table = new Entry[CAPACITY];
    }

    static class Entry<K, V> implements Map.Entry<K, V> {
        K key;
        V value;
        Entry<K, V> next;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() { return key; }
        public V getValue() { return value; }
        public V setValue(V value) {
            V old = this.value;
            this.value = value;
            return old;
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        try {
            get(key);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public V get(Object key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        int index = getIndex((K) key);
        Entry<K, V> entry = table[index];
        while (entry != null) {
            if (entry.key.equals(key)) {
                return entry.value;
            }
            entry = entry.next;
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }

        if (size >= table.length * LOAD_FACTOR) {
            resize();
        }

        int index = getIndex(key);
        Entry<K, V> entry = table[index];

        while (entry != null) {
            if (entry.key.equals(key)) {
                entry.value = value;
                return entry.value;
            }
            entry = entry.next;
        }

        Entry<K, V> newEntry = new Entry<>(key, value);
        newEntry.next = table[index];
        table[index] = newEntry;
        size++;
        return value;
    }

    @Override
    public V remove(Object key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        int index = getIndex((K) key);
        Entry<K, V> entry = table[index];
        V removed = null;
        while (entry != null) {
            if (entry.key != null && entry.key.equals(key)) {
                removed = entry.value;
                if (entry.next == null) {
                    table[index] = entry.next;
                }

                entry.key = null;
                entry.value = null;
                entry.next = null;
                size--;
            }
            entry = entry.next;
        }

        return removed;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {

    }

    @Override
    public void clear() {
        Arrays.fill(table, null);
        size = 0;
        entrySet = null;
    }

    @Override
    public Set<K> keySet() {
        Set<K> keys = new HashSet<>();
        for (Map.Entry<K, V> entry : entrySet()) {
            keys.add(entry.getKey());
        }
        return keys;
    }

    @Override
    public Collection<V> values() {
        Collection<V> values = new HashSet<>();
        for (Map.Entry<K, V> entry : entrySet()) {
            values.add(entry.getValue());
        }
        return values;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> es;
        return (es = entrySet) == null ? (entrySet = new EntrySet()) : es;
    }

    private class EntrySet extends AbstractSet<Map.Entry<K, V>> {
        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntryIterator();
        }

        @Override
        public int size() {
            return size;
        }
    }

    private class EntryIterator implements Iterator<Map.Entry<K, V>> {
        private int index = 0;
        private Entry<K, V> current;
        private Entry<K, V> next;

        public EntryIterator() {
            findNext();
        }

        private void findNext() {
            while (index < table.length) {
                if (next == null) {
                    next = table[index++];
                } else {
                    next = next.next;
                }
                if (next != null) return;
            }
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public Map.Entry<K, V> next() {
            if (!hasNext()) throw new NoSuchElementException();
            current = next;
            findNext();
            return current;
        }
    }


    private void resize() {
        Entry<K, V>[] oldTable = table;
        table = new Entry[oldTable.length * 2];
        size = 0;

        for (Entry<K, V> entry : oldTable) {
            while (entry != null) {
                put(entry.key, entry.value);
                entry = entry.next;
            }
        }
    }

    private int getIndex(K key) {
        return Math.abs(key.hashCode()) % table.length;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CustomHashMap{size=").append(size).append(", entries=[");

        boolean first = true;
        for (Entry<K, V> entry : table) {
            while (entry != null) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append(entry.key).append("=").append(entry.value);
                first = false;
                entry = entry.next;
            }
        }

        sb.append("]}");
        return sb.toString();
    }
}

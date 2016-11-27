package collections;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class MapHash<K, V> implements Map<K, V> {
    private int size;
    private float loadFactor;
    private int threshold;
    private int count;
    Node<K, V>[] table;
    static final int DEFAULT_CAPACITY = 16;
    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    public MapHash(int size, float loadFactor) {
        checkSize(size);
        checkLoadFactor(loadFactor);
        this.size = size;
        this.loadFactor = loadFactor;
        this.threshold = (int)(loadFactor * size);
        table = new Node[size];
    }

    public MapHash() {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public MapHash(int size) {
        this(size, DEFAULT_LOAD_FACTOR);
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean containsKey(Object key) {
        return getNode(hash(key), (K) key) == null;
    }

    public boolean containsValue(Object value) {
        return false;
    }

    public V get(Object key) {
        Node<K, V> node = getNode(hash(key), (K) key);

        return node == null
                ? null
                : node.getValue();
    }

    public V put(K key, V value) {
        if (key == null) {
            throw new IllegalStateException();
        }

        checkCapacity();
        Node<K, V> curNode;

        if ((curNode  = getNode(hash(key), key)) == null) {
            table[tableNum(hash(key))] = new Node<>(hash(key), key, value);
            count++;
        } else {
            while (curNode.next != null) {
                if (curNode.key.equals(key)) {
                    V oldVal = curNode.value;
                    curNode.value = value;
                    return oldVal;
                }
                curNode = curNode.next;
            }

            curNode.next = new Node<>(hash(key), key, value);
        }

        return value;
    }

    public V remove(Object key) {
        Node<K, V> curNode;
        if ((curNode = getNode(hash(key), (K) key)) == null) {
            return null;
        } else {
            if (curNode.key.equals(key)) {
                table[tableNum(curNode.hash)] = null;
            } else {
                while (!curNode.next.getKey().equals(key)) {
                    curNode = curNode.next;
                }
                curNode.next = null;
            }
            count--;
            return curNode.value;
        }
    }

    public void clear() {
        table = (Node<K, V>[]) new Object[table.length];
    }

    static class Node<K, V> implements Map.Entry<K, V> {
        final int hash;
        final K key;
        V value;
        Node<K, V> next;

        public Node(int hash, K key, V value) {
            this.hash = hash;
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Node<?, ?> node = (Node<?, ?>) o;

            if (hash != node.hash) return false;
            if (key != null ? !key.equals(node.key) : node.key != null) return false;
            return value != null ? value.equals(node.value) : node.value == null;

        }

        public Node<K, V> getNext() {
            return next;
        }

        public void setNext(Node<K, V> next) {
            this.next = next;
        }
    }

    private int tableNum(int hash) {
        return hash % (table.length - 1);
    }

    private int hash(Object key) {
        return (key == null)
                ? 0
                :  key.hashCode() ^ 31;
    }

    private void checkCapacity() {
        if ((count + 1) > threshold) {
            Node<K, V>[] newTable = new Node[size*=2];
            threshold = (int) (loadFactor * size);
            Node<K, V> tempNode;

            if (count == 0) return;

            for (Node node : table) {
                if (node!=null) {
                    newTable[tableNum(node.hash)] = node;

                    while (node.next != null) {
                        tempNode = node.next;
                        newTable[tableNum(tempNode.hash)] = tempNode;
                        node.next = null;
                    }
                }
            }
        }
    }

    private void checkSize(int size) {
        assert (size > DEFAULT_CAPACITY);
    }

    private void checkLoadFactor(float loadFactor) {
        assert (loadFactor > 0);
    }

    private Node<K, V> getNode(int hash, K key) {
        if (table[tableNum(hash)] == null) {
            return null;
        } else {
            Node<K, V> current = table[tableNum(hash)];

            while (current.next != null) {
                if (current.getKey().equals(key)) {
                    return current;
                }

                current = current.next;
            }

            return current;
        }
    }

    public Set<Entry<K, V>> entrySet() {
        return null;
    }

    public void putAll(Map<? extends K, ? extends V> m) { }

    public Set<K> keySet() { return null; }

    public Collection<V> values() {
        return null;
    }
}
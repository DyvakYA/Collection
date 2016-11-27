package collections;

import java.util.*;

public class SetTree<E extends Comparable<E>> implements Set<E> {
    private Node<E> root = null;
    private int size;

    public int size() { return size; }

    public boolean isEmpty() { return size == 0; }

    public boolean contains(Object o) {
        return getNode((E) o) != null;
    }

    public boolean add(E e) {
        if (root == null) {
            root = new Node<>(e);
        }

        Node<E> current = root;

        while (current != null) {
            int compareRes = e.compareTo(current.value);

            if (compareRes == 0) {
                return false;
            } else if (compareRes < 0) {
                if (current.left == null) {
                    Node<E> addingNode = new Node<>(e);
                    current.left = addingNode;
                    addingNode.parent = current;
                    size++;
                    return true;
                }
                current = current.left;
            } else {
                if (current.right == null) {
                    Node<E> addingNode = new Node<>(e);
                    current.right = addingNode;
                    addingNode.parent = current;
                    size++;
                    return true;
                }
                current = current.right;
            }
        }

        return false;
    }

    public boolean remove(Object o) {
        Node<E> current = getNode((E) o);

        if (current == null) {
            return false;
        }

        Node<E> parentNode = current.parent;

        if (root == current) {
            root = null;
        } else if (current.left == null && current.right == null) {
            if (parentNode.left == current) {
                parentNode.left = null;
            } else {
                parentNode.right = null;
            }
        } else if (current.left != null && current.right != null) {
            Node<E> highestNode = formHighestNode(current);

            if (parentNode.left == current) {
                parentNode.left = highestNode;
            } else {
                parentNode.right = highestNode;
            }
        } else if (current.left != null) {
            if (parentNode.left == current) {
                parentNode.left = current.left;
            } else {
                parentNode.right = current.left;
            }
        } else {
            if (parentNode.left == current) {
                parentNode.left = current.right;
            } else {
                parentNode.right = current.right;
            }
        }

        return true;
    }

    private Node<E> formHighestNode(Node<E> deleted) {
        Node<E> current = deleted.right;
        Node<E> insertable = deleted.left;

        while (current != null) {
            int compareRes = insertable.value.compareTo(current.value);

            if (current.right == null) {
                current.right = insertable;
                insertable.parent = current;
            } else if (compareRes < 0) {
                if (current.left == null) {
                    current.left = insertable;
                    insertable.parent = current;
                }
                current = current.left;
            } else  {
                current = current.right;
            }
        }

        return current;
    }


    public void clear() {
        root = null;
    }

    private Node<E> getNode(E val) {
        if (root == null) {
            return null;
        }

        Node<E> current = root;

        int compareRes = val.compareTo(current.value);

        while (current != null) {
            if (compareRes == 0) {
                return current;
            } else if (compareRes < 0) {
                current = current.left;
            } else if (compareRes > 0) {
                current = current.right;
            }
        }

        return null;
    }

    static class Node<E> {
        private Node<E> left;
        private Node<E> right;
        private Node<E> parent;
        E value;

        public Node(SetTree.Node<E> parent, E value) {
            this.parent = parent;
            this.value = value;
        }

        public Node(E value) {
            this.value = value;
        }
    }

    private class CurIterator<E> implements java.util.Iterator<E>{
        ArrayList<E> currentList;
        int current;

        public CurIterator() {
            currentList = new ArrayList<>();
            initNode((Node<E>) root);
        }

        public boolean hasNext() {
            return current + 1 < currentList.size() && currentList.get(current + 1) != null;
        }

        public E next() {
            return currentList.get(++current);
        }

        private void initNode(Node<E> node) {
            if (node.left != null) {
                currentList.add(node.left.value);
                initNode(node.left);
            }

            if (node.right != null) {
                currentList.add(node.right.value);
                initNode(node.right);
            }
        }
    }

    public CurIterator<E> iterator() {return new CurIterator<E>(); }

    public boolean containsAll(Collection<?> c) { return false; }

    public boolean addAll(Collection<? extends E> c) { return false; }

    public boolean retainAll(Collection<?> c) { return false; }

    public boolean removeAll(Collection<?> c) { return false; }

    public Object[] toArray() { return new Object[0]; }

    public <T> T[] toArray(T[] a) { return null; }
}
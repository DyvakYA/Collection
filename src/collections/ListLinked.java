package collections;

import java.util.*;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class ListLinked<T> implements List<T>, Queue<T> {
    private int size;
    private int modCount;
    private Node<T> first;
    private Node<T> last;

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        return (getIndex((T)o) != -1);
    }

    @Override
    public void forEach(Consumer<? super T> action) {
    }

    @Override
    public Object[] toArray() {
        Object[] outArr = new Object[size];
        int i = 0;

        for (Node<T> cur = first; cur != null; cur = cur.next) {
            outArr[i++] = cur.item;
        }
        return new Object[size];
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return null;
    }

    @Override
    public boolean add(T t) {
        linkLast(t);
        return true;
    }

    @Override
    public boolean offer(T t) {
        return add(t);
    }

    @Override
    public T remove() {
        final Node<T> firstObj = first;

        if (first == null) {
            throw new NoSuchElementException();
        }

        return firstObj.item;
    }

    @Override
    public T poll() {
        final Node<T> outFirst = first;

        return (outFirst == null) ? null : outFirst.item;
    }

    @Override
    public T element() {
        if (first == null) {
            throw new NoSuchElementException();
        }

        final Node<T> outFirst = first;

        return outFirst.item;
    }

    @Override
    public T peek() {
        final Node<T> outFirst = first;
        return outFirst == null ? null : outFirst.item;
    }

    @Override
    public boolean remove(Object o) {
        Node<T> delObj = getByItem((T) o);
        if (delObj == null) {
            return false;
        } else {
            unlinkElement(delObj);
            return true;
        }
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return false;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public void replaceAll(UnaryOperator<T> operator) {

    }

    @Override
    public void sort(Comparator<? super T> c) {

    }

    @Override
    public void clear() {
        for (Node<T> x = first; x != null; ) {
            Node<T> next = x.next;
            x.item = null;
            x.next = null;
            x.prev = null;
            x = next;
        }
        first = last = null;
        size = 0;
        modCount++;
    }

    @Override
    public T get(int index) {
        return getByIndex(index).item;
    }

    public T getFirst() {
        if (isEmpty()) {
            throw new IndexOutOfBoundsException("List is empty");
        }

        return getByIndex(0).item;
    }

    public T getLast() {
        if (isEmpty()) {
            throw new IndexOutOfBoundsException("List is empty");
        }

        return getByIndex(size - 1).item;
    }

    @Override
    public T set(int index, T element) {
        checkIndex(index);

        Node<T> obj = getByIndex(index);
        T oldVal = obj.item;
        obj.item = element;
        return oldVal;
    }

    @Override
    public void add(int index, T element) {
        checkIfPosition(index);

        if (index == 0) {
            linkFirst(element);
        } else if (index == size) {
            linkLast(element);
        } else {
            Node<T> elemBefore = getByIndex(index);
            Node<T> elemNext = elemBefore.next;
            Node<T> insertElem = new Node<T>(elemBefore, element, elemNext);
            elemBefore.next = insertElem;
            elemNext.prev = insertElem;
            size++;
        }
    }

    @Override
    public T remove(int index) {
        checkIndex(index);
        Node<T> deletedObj;

        if (index==0) {
            return unlinkFirst();
        } else if (index==size-1) {
            return unlinkLast();
        } else {
            deletedObj = getByIndex(index);

            Node<T> beforeObj = deletedObj.prev;
            Node<T> afterObj = deletedObj.next;

            beforeObj.next = afterObj;
            afterObj.prev = beforeObj;
            size--;
            modCount++;
        }


        return deletedObj.item;
    }

    @Override
    public int indexOf(Object o) {
        return getIndex((T) o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return getLastIndex((T) o);
    }

    @Override
    public Iterator<T> iterator() {
        return new IteratorList(0);
    }

    @Override
    public ListIterator<T> listIterator() {
        return new IteratorList(0);
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        checkIfPosition(index);
        return new IteratorList(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return null;
    }

    @Override
    public Spliterator<T> spliterator() {
        return null;
    }

    private void linkFirst(T linkObj) {
        if (first == null) {
            linkToEmptyList(linkObj);
            return;
        }

        Node<T> f = first;
        Node<T> newNode = new Node<T>(null, linkObj, first);

        first = newNode;
        f.prev = newNode;

        size++;
        modCount++;
    }

    private void linkLast(T linkObj) {
        if (last == null) {
            linkToEmptyList(linkObj);
            return;
        }

        Node<T> f = last;
        Node<T> newNode = new Node<T>(last, linkObj, null);

        last = newNode;
        f.next = newNode;

        size++;
        modCount++;
    }

    private void linkBefore(T insertItem, Node<T> beforeElem) {
        final Node<T> prev = beforeElem.prev;
        final Node<T> newNode = new Node<T>(prev, insertItem, beforeElem);
        beforeElem.prev = newNode;

        if (prev == null) {
            first = newNode;
        } else {
            prev.next = newNode;
        }

        size++;
        modCount++;
    }

    public Node<T> getByIndex(int index) {
        checkIndex(index);
        Node<T> currentNode;

        if (index < ( size / 2 )) {
            currentNode = first;

            for (int i = 0; i < index; i++) {
                currentNode = currentNode.next;
            }

            return currentNode;
        } else {
            currentNode = last;

            for (int i = size - 1; i > index; i--) {
                currentNode = currentNode.prev;
            }

            return currentNode;
        }
    }

    public Node<T> getByItem(T item) {
        Node<T> curElem = first;

        while (curElem!=null) {
            if (curElem.item.equals(item)) {
                return curElem;
            }
            curElem = curElem.next;
        }

        return null;
    }

    public int getIndex(T obj) {
        Node<T> currentNode;

        currentNode = first;

        for (int i = 0; i < size; i++) {
            if (currentNode.equals(obj)) {
                return i;
            }
        }

        return -1;
    }

    public int getLastIndex(T obj) {
        Node<T> currentNode;

        currentNode = last;

        for (int i = 0; i < size; i++) {
            if (currentNode.equals(obj)) {
                return i;
            }
        }

        return -1;
    }

    private void linkToEmptyList(T firstElem) {
        first = last = new Node<>(null, firstElem, null);
        size++;
        modCount++;
    }

    private void checkIndex(int index) {
        if ((index < 0) || (index >= size)) {
            throw new IndexOutOfBoundsException("Wrong index :"+index+", when size :"+size);
        }
    }

    private void checkIfPosition(int index) {
        if ((index < 0) || (index > size)) {
            throw new IndexOutOfBoundsException("Wrong index :"+index+", when size :"+size);
        }
    }

    private T unlinkFirst() {
        final Node<T> firstObj = first;

        if (isEmpty()) {
            throw new NoSuchElementException();
        } else if (size == 1) {
            first = last = null;
        } else {
            first = firstObj.next;
            first.prev = null;
        }

        size--;
        modCount++;

        return firstObj.item;
    }

    private T unlinkLast() {
        final Node<T> lastObj = last;

        if (isEmpty()) {
            throw new NoSuchElementException();
        } else if (size == 1) {
            first = last = null;
        } else {
            last = lastObj.prev;
            last.next = null;
        }

        size--;
        modCount++;

        return lastObj.item;
    }

    private T unlinkElement(Node<T> element) {
        T del = element.item;
        Node<T> prev = element.prev;
        Node<T> next = element.next;

        if (next == null) {
            last = prev;
        } else {
            next.prev = prev;
            element.next = null;
        }

        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
            element.prev = null;
        }

        element.item = null;

        size--;
        modCount++;

        return del;
    }

    private T unlinkFirst(Node<T> elem) {
        final T element = elem.item;
        final Node<T> next = elem.next;
        elem.item = null;
        elem.next = null;
        first = next;

        if (next == null) {
            last = null;
        }
        else {
            next.prev = null;
        }

        size--;
        modCount++;

        return element;
    }

    private T unlinkLast(Node<T> elem) {
        final T element = elem.item;
        final Node<T> prev = elem.prev;
        elem.item = null;
        elem.prev = null;
        last = prev;

        if (prev == null) {
            first = null;
        } else {
            prev.next = null;
        }

        size--;
        modCount++;

        return element;
    }

    private static class Node<T> {
        T item;
        Node<T> next;
        Node<T> prev;

        Node(Node<T> prev, T element, Node<T> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }

    private class IteratorList implements ListIterator<T> {
        private ListLinked.Node<T> lastReturned;
        private ListLinked.Node<T> next;
        private int nextIndex;
        private int expectedModCount = modCount;

        IteratorList(int index) {
            next = (index == size) ? null : getByIndex(index);
            nextIndex = index;
        }

        public boolean hasNext() {
            return nextIndex < size;
        }

        public T next() {
            checkForComodification();
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            lastReturned = next;
            next = next.next;
            nextIndex++;
            return lastReturned.item;
        }

        public boolean hasPrevious() {
            return nextIndex > 0;
        }

        public T previous() {
            checkForComodification();
            if (!hasPrevious()) {
                throw new NoSuchElementException();
            }

            lastReturned = next = (next == null) ? last : next.prev;
            nextIndex--;
            return lastReturned.item;
        }

        public int nextIndex() {
            return nextIndex;
        }

        public int previousIndex() {
            return nextIndex - 1;
        }

        public void remove() {
            checkForComodification();
            if (lastReturned == null) {
                throw new IllegalStateException();
            }

            ListLinked.Node<T> lastNext = lastReturned.next;
            unlinkElement(lastReturned);
            if (next == lastReturned) {
                next = lastNext;
            }
            else {
                nextIndex--;
            }

            lastReturned = null;
            expectedModCount++;
        }

        public void set(T e) {
            if (lastReturned == null) {
                throw new IllegalStateException();
            }

            checkForComodification();
            lastReturned.item = e;
        }

        public void add(T e) {
            checkForComodification();
            lastReturned = null;
            if (next == null)
                linkLast(e);
            else
                linkBefore(e, next);
            nextIndex++;
            expectedModCount++;
        }


        final void checkForComodification() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }
}
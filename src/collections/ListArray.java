package collections;

import java.util.*;

public class ListArray<T> implements List<T> {
    private final int DEFAULT_SIZE = 10;
    private int size;
    private Object[] elements;
    private int modCount;

    public ListArray() {
        elements = new Object[DEFAULT_SIZE];
    }

    public ListArray(int size) {
        elements = size > 0
                ? new Object[size]
                : new Object[DEFAULT_SIZE];
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
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    @Override
    public Iterator<T> iterator() {
        return new CurrentIterator();
    }

    @Override
    public Object[] toArray() {
        return Arrays.copyOf(elements, size);
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        if (a.length < size) {
            return (T1[]) Arrays.copyOf(elements, size, a.getClass());
        }

        System.arraycopy(elements, 0, a, 0, size);
        if (a.length > size) {
            a[size] = null;
        }

        return a;
    }

    @Override
    public boolean add(T t) {
        ensureCapacity(size + 1);
        elements[size++] = t;
        modCount++;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        for (int index = 0; index < size; index++) {
            if (o.equals(elements[index])) {
                remove(index);
                return true;
            }
        }

        return false;
    }

    @Override
    public void clear() {
        modCount++;

        for (int i = 0; i < size; i++) {
            elements[i] = null;
        }

        size = 0;
    }

    @Override
    public T get(int index) {
        checkPosition(index);
        return (T) elements[index];
    }

    @Override
    public T set(int index, T element) {
        checkPosition(index);
        T returnValue = (T) elements[index];
        elements[index] = element;
        return returnValue;
    }

    @Override
    public void add(int index, T element) {
        checkIndex(index);
        ensureCapacity(size + 1);
        int toAdd = size - index;
        System.arraycopy(elements, index, elements, index + 1, toAdd);
        elements[index] = element;
        size++;
        modCount++;
    }

    @Override
    public T remove(int index) {
        checkPosition(index);
        T returnValue = (T) elements[index];

        int shiftNum = size - index - 1;

        if (shiftNum > 0) {
            System.arraycopy(elements, index+1, elements, index, shiftNum);
        }

        elements[--size] = null;
        modCount++;

        return returnValue;
    }

    @Override
    public int indexOf(Object o) {
        if (o == null) {
            for (int i = 0; i < size; i++) {
                if (elements[i]==null) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < size; i++) {
                if (o.equals(elements[i])) {
                    return i;
                }
            }
        }

        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        if (o == null) {
            for (int i = size - 1; i > 0; i--) {
                if (elements[i]==null) {
                    return i;
                }
            }
        } else {
            for (int i = size - 1; i > 0; i--) {
                if (o.equals(elements[i])) {
                    return i;
                }
            }
        }

        return -1;
    }

    @Override
    public ListIterator<T> listIterator() {
        return new CurrentListIterator(0);
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        checkPosition(index);

        return new CurrentListIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return null;
    }

    private void ensureCapacity(int neededSize) {
        if (neededSize > elements.length) {
            growList(neededSize);
        }
    }

    private void growList(int newSize) {
        int oldCapacity = elements.length;

        int newCapacity = oldCapacity * 2;

        if (newCapacity - newSize < 0) {
            newCapacity = newSize;
        }

        elements = Arrays.copyOf(elements, newCapacity);
    }

    private void checkIndex(int inputIndex) {
        if (inputIndex < 0 || inputIndex > size) {
            throw new IndexOutOfBoundsException("Actual size of collection is : "+inputIndex+" input :"+inputIndex);
        }
    }

    private void checkPosition(int inputIndex) {
        if (inputIndex < 0 || inputIndex >= size) {
            throw new IndexOutOfBoundsException("Actual size of collection is : "+inputIndex+" input :"+inputIndex);
        }
    }

    private class CurrentIterator implements Iterator<T> {
        int cursor;
        int lastRet = -1;
        int expectedModCount = modCount;

        public boolean hasNext() {
            return cursor != size;
        }

        public T next() {
            checkForComodification();
            int i = cursor;

            if (i >= size) {
                throw new NoSuchElementException();
            }

            Object[] elementData = ListArray.this.elements;

            if (i >= elementData.length) {
                throw new ConcurrentModificationException();
            }

            return (T) elementData[lastRet = ++cursor];
        }

        public void remove() {
            if (lastRet < 0) {
                throw new IllegalStateException();
            }

            checkForComodification();

            try {
                ListArray.this.remove(lastRet);
                cursor = lastRet;
                lastRet = -1;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

        final void checkForComodification() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }

    private class CurrentListIterator extends CurrentIterator implements ListIterator<T> {
        CurrentListIterator(int index) {
            super();
            cursor = index;
        }

        public boolean hasPrevious() {
            return cursor != 0;
        }

        public int nextIndex() {
            return cursor;
        }

        public int previousIndex() {
            return cursor - 1;
        }

        public T previous() {
            checkForComodification();
            int i = cursor - 1;

            if (i < 0) {
                throw new NoSuchElementException();
            }

            Object[] elementData = ListArray.this.elements;
            if (i >= elementData.length) {
                throw new ConcurrentModificationException();
            }

            cursor = i;
            return (T) elementData[lastRet = i];
        }

        public void set(T e) {
            if (lastRet < 0) {
                throw new IllegalStateException();
            }

            checkForComodification();

            try {
                ListArray.this.set(lastRet, e);
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

        public void add(T e) {
            checkForComodification();

            try {
                int i = cursor;
                ListArray.this.add(i, e);
                cursor = i + 1;
                lastRet = -1;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
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
    public boolean retainAll(Collection<?> c) {
        return false;
    }
}

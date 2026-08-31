package chapter5.good;

import java.util.Arrays;

public final class GoodGenericStack<E> {
    private Object[] elements;
    private int size;
    private static final int DEFAULT_INITIAL_CAPACITY = 8;

    public GoodGenericStack() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(E element) {
        ensureCapacity();
        elements[size++] = element;
    }

    public E pop() {
        if (size == 0) {
            throw new IllegalStateException("empty stack");
        }
        // Safe: only E values ever enter elements (push(E)), so the read-back
        // cast cannot observe a foreign type.
        @SuppressWarnings("unchecked")
        E result = (E) elements[--size];
        elements[size] = null;
        return result;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void ensureCapacity() {
        if (size == elements.length) {
            elements = Arrays.copyOf(elements, 2 * size + 1);
        }
    }
}
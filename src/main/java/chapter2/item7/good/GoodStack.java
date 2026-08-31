package chapter2.item7.good;

public final class GoodStack {
    private Object[] elements = new Object[4];
    private int size;

    public void push(Object value) {
        if (size == elements.length) {
            Object[] expanded = new Object[elements.length * 2];
            System.arraycopy(elements, 0, expanded, 0, size);
            elements = expanded;
        }
        elements[size++] = value;
    }

    public Object pop() {
        Object result = elements[--size];
        elements[size] = null;
        return result;
    }
}

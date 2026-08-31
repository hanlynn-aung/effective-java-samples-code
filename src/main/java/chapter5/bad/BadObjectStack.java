package chapter5.bad;

import java.util.ArrayList;
import java.util.List;

public final class BadObjectStack {
    private final List<Object> elements = new ArrayList<>();

    public void push(Object value) {
        elements.add(value);
    }

    public Object pop() {
        return elements.remove(elements.size() - 1);
    }

    public boolean isEmpty() {
        return elements.isEmpty();
    }
}
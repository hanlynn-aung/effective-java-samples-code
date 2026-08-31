package chapter4.good;

import java.util.ArrayDeque;
import java.util.Deque;

public final class GoodStack {
    private final Deque<String> values = new ArrayDeque<>();

    public void push(String value) { values.push(value); }
    public String pop() { return values.pop(); }
    public boolean isEmpty() { return values.isEmpty(); }
}

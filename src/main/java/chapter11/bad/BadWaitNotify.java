package chapter11.bad;

import java.util.ArrayDeque;
import java.util.Deque;

public final class BadWaitNotify {

    private final Deque<Integer> buffer = new ArrayDeque<>();
    private final int capacity;

    public BadWaitNotify(int capacity) {
        this.capacity = capacity;
    }

    public synchronized void put(int value) throws InterruptedException {
        while (buffer.size() == capacity) {
            wait();
        }
        buffer.addLast(value);
        notifyAll();
    }

    public synchronized int take() throws InterruptedException {
        while (buffer.isEmpty()) {
            wait();
        }
        int value = buffer.removeFirst();
        notifyAll();
        return value;
    }

    public synchronized int size() {
        return buffer.size();
    }
}
package chapter11.good;

import java.util.concurrent.atomic.AtomicInteger;

public final class GoodSynchronizedCounter {

    private final AtomicInteger count = new AtomicInteger();

    public int increment() {
        return count.incrementAndGet();
    }

    public int value() {
        return count.get();
    }
}
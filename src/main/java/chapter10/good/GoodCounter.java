package chapter10.good;

import java.util.concurrent.atomic.AtomicInteger;

public final class GoodCounter {
    private final AtomicInteger value = new AtomicInteger();

    public void increment() { value.incrementAndGet(); }
    public int value() { return value.get(); }
}

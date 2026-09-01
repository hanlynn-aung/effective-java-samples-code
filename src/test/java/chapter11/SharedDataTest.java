package chapter11;

import chapter11.bad.BadSharedCounter;
import chapter11.good.GoodMonitoredCounter;
import chapter11.good.GoodSynchronizedCounter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class SharedDataTest {

    private static void hammer(Runnable increment, Runnable read) {
        int threads = 8;
        int perThread = 100_000;
        ExecutorService exec = Executors.newFixedThreadPool(threads);
        try {
            for (int t = 0; t < threads; t++) {
                exec.submit(() -> {
                    for (int i = 0; i < perThread; i++) {
                        increment.run();
                        read.run();
                    }
                });
            }
        } finally {
            exec.shutdown();
        }
        try {
            exec.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    @DisplayName("Bad: an unsynchronized counter loses updates under concurrency")
    void badLosesUpdates() {
        BadSharedCounter bad = new BadSharedCounter();
        hammer(bad::increment, bad::value);
        int expected = 8 * 100_000;
        assertNotEquals(expected, bad.value(),
                "unsynchronized read-modify-write lost updates");
    }

    @Test
    @DisplayName("Good: AtomicInteger counter is exact under the same hammering")
    void goodAtomicIsExact() {
        GoodSynchronizedCounter good = new GoodSynchronizedCounter();
        hammer(good::increment, good::value);
        assertEquals(8 * 100_000, good.value());
    }

    @Test
    @DisplayName("Good: a synchronized counter is exact too")
    void goodSynchronizedIsExact() {
        GoodMonitoredCounter good = new GoodMonitoredCounter();
        List<Integer> reads = new ArrayList<>();
        Runnable read = () -> { synchronized (reads) { reads.add(good.value()); } };
        hammer(good::increment, read);
        assertEquals(8 * 100_000, good.value());
    }
}
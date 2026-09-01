package chapter11;

import chapter11.bad.BadWaitNotify;
import chapter11.good.GoodUtility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConcurrencyUtilityTest {

    @Test
    @DisplayName("Good: BlockingQueue coordinates producers+consumers with no hand-rolled wait/notify")
    void goodBlockingQueue() throws Exception {
        GoodUtility good = new GoodUtility();
        int producers = 4;
        int each = 5_000;
        int produced = producers * each;
        AtomicLong total = new AtomicLong();

        ExecutorService exec = Executors.newFixedThreadPool(8);
        try {
            for (int p = 0; p < producers; p++) {
                exec.submit(() -> {
                    try {
                        for (int i = 0; i < each; i++) {
                            good.put(i);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
            int consumers = 8;
            for (int c = 0; c < consumers; c++) {
                exec.submit(() -> {
                    try {
                        for (int i = 0; i < produced / 8; i++) {
                            total.addAndGet(good.take());
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
            exec.shutdown();
            exec.awaitTermination(30, TimeUnit.SECONDS);
        } finally {
            exec.shutdownNow();
        }

        // Each producer emits 0..(each-1), so the whole stream is the sequence
        // 0..(each-1) repeated `producers` times.
        long expected = producers * ((long) each * (each - 1) / 2L);
        assertEquals(expected, total.get());
    }

    @Test
    @DisplayName("Both: the hand-rolled wait/notify and the utility expose the same operations")
    void badWaitNotifyExposesOps() throws InterruptedException {
        BadWaitNotify bad = new BadWaitNotify(3);
        bad.put(7);
        assertEquals(7, bad.take());
        assertEquals(0, bad.size());

        GoodUtility good = new GoodUtility();
        good.put(9);
        assertEquals(9, good.take());
        assertEquals(0, good.size());
    }
}
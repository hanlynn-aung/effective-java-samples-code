package chapter11.good;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public final class GoodSchedulerNeutral {

    public boolean startAndWait(long timeoutMillis) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        new Thread(() -> {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                latch.countDown();
            }
        }).start();
        // Correct utility: blocks efficiently, not scheduler-dependent, and
        // honours a timeout (can't hang forever if the task fails).
        return latch.await(timeoutMillis, TimeUnit.MILLISECONDS);
    }
}
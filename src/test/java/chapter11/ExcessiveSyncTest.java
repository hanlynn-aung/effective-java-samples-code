package chapter11;

import chapter11.bad.BadHoldsLockInCallback;
import chapter11.good.GoodMinimalSync;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExcessiveSyncTest {

    @Test
    @DisplayName("Bad: a slow alien call under lock blocks unrelated work that needs the same lock")
    void badAlienCallUnderLockStallsOtherWork() throws Exception {
        BadHoldsLockInCallback bad = new BadHoldsLockInCallback();
        CountDownLatch listenerStarted = new CountDownLatch(1);
        CountDownLatch releaseListener = new CountDownLatch(1);
        BadHoldsLockInCallback.Listener slow = () -> {
            listenerStarted.countDown();
            try {
                releaseListener.await(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
        bad.addListener(slow);

        ExecutorService exec = Executors.newFixedThreadPool(2);
        Future<?> setReady;
        Future<?> add;
        AtomicBoolean added = new AtomicBoolean(false);
        try {
            setReady = exec.submit(bad::setReady);
            listenerStarted.await(2, TimeUnit.SECONDS);

            // While setReady() holds the lock calling the slow listener, another
            // thread that needs the same lock (addListener) cannot proceed.
            add = exec.submit(() -> {
                bad.addListener(slow);
                added.set(true);
            });
            Thread.sleep(200);
            // The bad design blocks addListener() because the lock is held by the alien call.
            assertFalse(added.get(), "addListener() could not proceed - lock held by alien call");
        } finally {
            releaseListener.countDown();
            exec.shutdownNow();
        }
        setReady.get(5, TimeUnit.SECONDS);
        add.get(5, TimeUnit.SECONDS);
        assertTrue(added.get(), "after release the listener could be added");
    }

    @Test
    @DisplayName("Good: snapshot + call listeners outside any lock, so a slow listener can't block others")
    void goodCallsOutsideLock() throws Exception {
        GoodMinimalSync good = new GoodMinimalSync();
        CountDownLatch started = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        GoodMinimalSync.Listener slow = () -> {
            started.countDown();
            try {
                release.await(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
        good.addListener(slow);

        ExecutorService exec = Executors.newFixedThreadPool(2);
        try {
            Future<?> setReady = exec.submit(good::setReady);
            started.await(2, TimeUnit.SECONDS);

            // While the slow listener runs, another thread can freely add (no lock held).
            AtomicBoolean added = new AtomicBoolean(false);
            Future<?> add = exec.submit(() -> {
                good.addListener(slow);
                added.set(true);
            });
            Thread.sleep(200);
            assertTrue(added.get(), "addListener() proceeded despite a slow listener running");
            release.countDown();
            setReady.get(5, TimeUnit.SECONDS);
            add.get(5, TimeUnit.SECONDS);
        } finally {
            exec.shutdownNow();
        }
    }
}
package chapter11.demo;

import chapter11.bad.BadWaitNotify;
import chapter11.good.GoodLazyHolder;
import chapter11.good.GoodLazyInit;
import chapter11.good.GoodSchedulerNeutral;
import chapter11.good.GoodThreadDoc;
import chapter11.good.GoodUtility;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Demonstrates items 81-84: concurrency utilities, thread-safety docs, lazy
 * initialization, and not depending on the thread scheduler.
 */
public final class ConcurrencyUtilitiesDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Item 81: prefer concurrency utilities to wait/notify ===");
        GoodUtility utility = new GoodUtility();
        ExecutorService exec = Executors.newFixedThreadPool(4);
        try {
            exec.submit(() -> {
                try {
                    for (int i = 0; i < 5; i++) utility.put(i);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            for (int i = 0; i < 5; i++) {
                System.out.println("    took " + utility.take());
            }
        } finally {
            exec.shutdownNow();
        }
        BadWaitNotify manual = new BadWaitNotify(3);
        manual.put(1);
        System.out.println("    hand-rolled wait/notify also works but is error-prone; took "
                + manual.take());

        System.out.println();
        System.out.println("=== Item 82: document thread safety ===");
        GoodThreadDoc doc = new GoodThreadDoc();
        doc.put("k", "v");
        System.out.println("    documented thread-safe cache get('k') = " + doc.get("k"));

        System.out.println();
        System.out.println("=== Item 83: use lazy initialization judiciously ===");
        GoodLazyInit dcl = new GoodLazyInit();
        System.out.println("    DCL instance " + dcl.getHeavy().get("expensive"));
        System.out.println("    holder singleton " + GoodLazyHolder.getInstance().name());

        System.out.println();
        System.out.println("=== Item 84: don't depend on the thread scheduler ===");
        GoodSchedulerNeutral neutral = new GoodSchedulerNeutral();
        System.out.println("    latch awaited (30s timeout): " + neutral.startAndWait(30_000));
    }
}
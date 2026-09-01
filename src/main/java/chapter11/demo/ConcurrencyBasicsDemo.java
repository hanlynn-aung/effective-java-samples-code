package chapter11.demo;

import chapter11.bad.BadRawThread;
import chapter11.bad.BadSharedCounter;
import chapter11.good.GoodExecutor;
import chapter11.good.GoodMonitoredCounter;
import chapter11.good.GoodSynchronizedCounter;

/**
 * Demonstrates items 78-80: synchronizing shared data, avoiding excessive
 * synchronization, and preferring executors to raw threads.
 */
public final class ConcurrencyBasicsDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Item 78: synchronize access to shared mutable data ===");
        BadSharedCounter bad = new BadSharedCounter();
        GoodMonitoredCounter goodSync = new GoodMonitoredCounter();
        GoodSynchronizedCounter goodAtom = new GoodSynchronizedCounter();

        int threads = 8;
        int per = 100_000;
        Thread[] pool = new Thread[threads];
        for (var c : new Runnable[]{bad::increment, goodSync::increment, goodAtom::increment}) {
            for (int i = 0; i < threads; i++) {
                pool[i] = new Thread(new Repeat(c, per));
            }
            for (Thread t : pool) t.start();
            for (Thread t : pool) t.join();
            System.out.println("    bad(unsync)=" + bad.value()
                    + "  good(synchronized)=" + goodSync.value()
                    + "  good(atomic)=" + goodAtom.value()
                    + "  expected=" + (threads * per));
        }

        System.out.println();
        System.out.println("=== Item 80: prefer executors to raw threads ===");
        System.out.println("    bad raw threads ran: " + new BadRawThread().runPyramid(5)
                + " tasks");
        System.out.println("    good executor sum(0..4)^2 = " + new GoodExecutor().runPyramid(5));
    }

    private record Repeat(Runnable op, int times) implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < times; i++) {
                op.run();
            }
        }
    }
}
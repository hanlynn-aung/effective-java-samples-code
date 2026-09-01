package chapter11.bad;

import java.util.ArrayList;
import java.util.List;

public final class BadRawThread {

    private final List<Thread> threads = new ArrayList<>();

    public int runPyramid(int levels) {
        for (int i = 0; i < levels; i++) {
            Thread t = new Thread(() -> { });
            threads.add(t);
            t.start();
        }
        // No lifecycle management: can't easily wait for all, re-use, or limit
        // concurrency, and exceptions/leaks are hard to handle.
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return threads.size();
    }
}
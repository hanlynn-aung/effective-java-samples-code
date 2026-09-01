package chapter10.good;

import java.util.List;
import java.util.Objects;

public final class GoodHandle {

    private final java.util.concurrent.ConcurrentLinkedQueue<String> log =
            new java.util.concurrent.ConcurrentLinkedQueue<>();

    public void runSafe(Runnable task) {
        try {
            task.run();
        } catch (RuntimeException e) {
            Thread.currentThread().getUncaughtExceptionHandler()
                    .uncaughtException(Thread.currentThread(), e);
        }
    }

    public int safeParse(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            // Explicit, deliberate decision; distinguish error from a value.
            throw new IllegalArgumentException("not a number: " + value, e);
        }
    }

    public void record(List<String> rows) {
        for (String row : rows) {
            Objects.requireNonNull(row, "row");
            log.add(row);
        }
    }
}
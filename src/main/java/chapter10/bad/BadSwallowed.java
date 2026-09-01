package chapter10.bad;

import java.util.List;

public final class BadSwallowed {

    private final java.util.concurrent.ConcurrentLinkedQueue<String> log =
            new java.util.concurrent.ConcurrentLinkedQueue<>();

    public void runSafe(Runnable task) {
        try {
            task.run();
        } catch (RuntimeException e) {
            // deliberately ignore - the caller asked for a safe run
        }
    }

    public int safeParse(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return -1; // swallow; caller can't tell "error" from a real -1
        }
    }

    public void record(List<String> rows) {
        for (String row : rows) {
            try {
                log.add(row);
            } catch (RuntimeException ignored) {
                // silent
            }
        }
    }
}
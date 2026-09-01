package chapter11.bad;

public final class BadBusyWait {

    private volatile boolean done;

    public void startWork() {
        new Thread(() -> {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            done = true;
        }).start();
    }

    public boolean waitForDone() {
        // BAD: busy-spin with a sleep - depends on the scheduler allocating
        // time slices; wastes CPU, is timing-sensitive and fragile.
        while (!done) {
            Thread.yield();
        }
        return true;
    }
}
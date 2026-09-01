package chapter11.good;

public final class GoodMonitoredCounter {

    private int count;

    public synchronized int increment() {
        return ++count;
    }

    public synchronized int value() {
        return count;
    }
}
package chapter11.bad;

public final class BadSharedCounter {

    private int count;

    public int increment() {
        return ++count;
    }

    public int value() {
        return count;
    }
}
package chapter4.good;

public final class GoodFinalCounter {
    private int count;

    public GoodFinalCounter() {
        this(0);
    }

    public GoodFinalCounter(int initial) {
        this.count = initial;
    }

    public void add(int amount) {
        count += amount;
    }

    public int count() {
        return count;
    }
}
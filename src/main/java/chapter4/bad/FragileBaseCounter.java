package chapter4.bad;

public class FragileBaseCounter {
    private int count;

    public FragileBaseCounter() {
        add(1);
    }

    public void add(int amount) {
        count += amount;
    }

    public int count() {
        return count;
    }
}
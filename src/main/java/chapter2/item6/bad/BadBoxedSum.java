package chapter2.item6.bad;

public final class BadBoxedSum {
    public long sum(int count) {
        Long total = 0L;
        for (int i = 0; i < count; i++) {
            total += i;
        }
        return total;
    }
}
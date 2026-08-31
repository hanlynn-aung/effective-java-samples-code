package chapter2.item6.good;

public final class GoodPrimitiveSum {
    public long sum(int count) {
        long total = 0L;
        for (int i = 0; i < count; i++) {
            total += i;
        }
        return total;
    }
}
package chapter9.good;

public final class GoodMeasureFirst {

    // Correct, simple, readable - measure first, optimise the proven cost.
    public long sumFirst(long[] values) {
        long total = 0;
        for (long value : values) {
            total += value;
        }
        return total;
    }
}
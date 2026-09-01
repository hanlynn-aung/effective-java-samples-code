package chapter9.bad;

public final class BadPreoptimize {

    // Hand-tuned, convoluted micro-op on an unmeasured path.
    public long sumFirst(long[] values) {
        long total = 0;
        int i = 0;
        for (; i < values.length; i++) {
            // Bit-twiddling "optimization" that changes nothing for correctness.
            total += values[i] & Long.MAX_VALUE;
        }
        return total;
    }
}
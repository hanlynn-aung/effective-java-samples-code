package chapter7.bad;

import java.util.Arrays;

public final class BadParallelSum {

    public long badParallelSum(int[] values) {
        long[] total = {0L};
        Arrays.stream(values).parallel()
                .forEach(v -> total[0] += v);
        return total[0];
    }

    public long sequentialSum(int[] values) {
        long total = 0;
        for (int v : values) {
            total += v;
        }
        return total;
    }
}
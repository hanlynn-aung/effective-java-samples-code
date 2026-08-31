package chapter7.good;

import java.util.Arrays;

public final class GoodParallelReduce {

    public long sum(int[] values) {
        return Arrays.stream(values).parallel().asLongStream().sum();
    }
}
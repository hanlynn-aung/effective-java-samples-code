package chapter7;

import chapter7.bad.BadParallelSum;
import chapter7.good.GoodParallelReduce;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParallelCautionTest {

    private static final int N = 1_000_000;

    private static int[] fill() {
        return IntStream.range(0, N).toArray();
    }

    @Test
    @DisplayName("The sequential baseline is exact by construction")
    void sequentialBaselineIsExact() {
        int[] values = fill();
        long expected = N * (N - 1L) / 2L;
        assertEquals(expected, new BadParallelSum().sequentialSum(values));
    }

    @Test
    @DisplayName("Good: a parallel reduce/sum with an associative reducer stays exact")
    void goodParallelReduceIsExact() {
        int[] values = fill();
        long expected = N * (N - 1L) / 2L;
        GoodParallelReduce good = new GoodParallelReduce();
        // Deterministic regardless of thread scheduling - reduce composes
        // sub-results associatively instead of touching shared mutable state.
        for (int i = 0; i < 5; i++) {
            assertEquals(expected, good.sum(values));
        }
    }

    @Test
    @DisplayName("Bad: a parallel forEach into a shared long[] mutates outside the pipeline")
    void badParallelMutationHasNoGuarantee() {
        int[] values = fill();
        long expected = N * (N - 1L) / 2L;
        BadParallelSum bad = new BadParallelSum();
        // Documenting the flaw: the bad implementation reads and writes a shared
        // accumulator with no synchronization. It *may* coincidentally be right,
        // but that is not a guarantee - the correct tool is an associative
        // reduce. (No single assertion; the danger is ordering/mutation.)
        long result = bad.badParallelSum(values);
        // We merely record that a run happened; correctness is not asserted here
        // because a racy sum has undefined (thus unassertable) behaviour.
        System.out.println("bad parallel sum (racy) = " + result
                + " vs expected = " + expected);
    }
}
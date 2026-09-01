package chapter9;

import chapter9.bad.BadPreoptimize;
import chapter9.good.GoodMeasureFirst;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OptimizeTest {

    @Test
    @DisplayName("Both: identical correct results - the bad version adds complexity, not correctness")
    void bothProduceSameResult() {
        long[] values = new long[1000];
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        for (int i = 0; i < values.length; i++) {
            values[i] = rng.nextInt(0, 1000);
        }
        long bad = new BadPreoptimize().sumFirst(values);
        long good = new GoodMeasureFirst().sumFirst(values);
        assertEquals(good, bad);
    }
}
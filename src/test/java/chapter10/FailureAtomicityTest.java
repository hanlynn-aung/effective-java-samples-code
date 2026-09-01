package chapter10;

import chapter10.bad.BadNonAtomic;
import chapter10.good.GoodAtomic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FailureAtomicityTest {

    @Test
    @DisplayName("Bad: a mid-batch failure leaves the object partially mutated")
    void badLeavesPartialState() {
        BadNonAtomic bad = new BadNonAtomic();
        List<String> batch = Arrays.asList("a", "b", null, "c");
        assertThrows(IllegalArgumentException.class, () -> bad.addBatch(batch));
        // "a" was already added before the null was hit - state is corrupted.
        assertEquals(2, bad.items().size());
    }

    @Test
    @DisplayName("Good: validate-then-commit means a failed call leaves the object unchanged")
    void goodIsAtomic() {
        GoodAtomic good = new GoodAtomic();
        List<String> batch = Arrays.asList("a", "b", null, "c");
        assertThrows(IllegalArgumentException.class, () -> good.addBatch(batch));
        assertEquals(0, good.items().size());
    }

    @Test
    @DisplayName("Good: a valid batch commits fully")
    void goodValidBatchCommits() {
        GoodAtomic good = new GoodAtomic();
        good.addBatch(Arrays.asList("x", "y"));
        assertEquals(2, good.items().size());
    }
}
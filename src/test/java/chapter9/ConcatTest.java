package chapter9;

import chapter9.bad.BadConcatLoop;
import chapter9.good.GoodStringBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConcatTest {

    @Test
    @DisplayName("Both: produce identical output - the difference is asymptotic cost")
    void bothProduceSameString() {
        String expected = "ababab";
        assertEquals(expected, new BadConcatLoop().repeat("ab", 3));
        assertEquals(expected, new GoodStringBuilder().repeat("ab", 3));
    }

    @Test
    @DisplayName("Both: large repeats are correct, though the += version costs O(n^2)")
    void bothLargeRepeatsCorrect() {
        String fragment = "x";
        int times = 10_000;
        String bad = new BadConcatLoop().repeat(fragment, times);
        String good = new GoodStringBuilder().repeat(fragment, times);
        assertEquals(times, bad.length());
        assertEquals(times, good.length());
    }
}
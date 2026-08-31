package chapter9;

import chapter9.bad.BadReinvent;
import chapter9.good.GoodUseLibraries;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LibraryTest {

    @Test
    @DisplayName("Bad: Math.random can be skewed per-bound; manual join reimplements String.join")
    void badReinventsWheel() {
        BadReinvent bad = new BadReinvent();
        for (int i = 0; i < 1000; i++) {
            int v = bad.randomWithRange(5);
            assertTrue(v >= 0 && v < 5, "randomWithRange escaped [0,5): " + v);
        }
        assertEquals("abc", bad.join(Arrays.asList("a", "b", "c")));
    }

    @Test
    @DisplayName("Good: libraries take care of edge cases - ThreadLocalRandom and String.join")
    void goodUsesLibraries() {
        GoodUseLibraries good = new GoodUseLibraries();
        for (int i = 0; i < 1000; i++) {
            int v = good.randomWithRange(5);
            assertTrue(v >= 0 && v < 5, "random escaped [0,5): " + v);
        }
        List<String> parts = Arrays.asList("x", "y");
        assertEquals("xy", good.join(parts));
        assertEquals(Math.max(1.0, 2.0), good.max(1.0, 2.0));
    }
}
package chapter9;

import chapter9.bad.BadWideScope;
import chapter9.good.GoodNarrowScope;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScopeTest {

    @Test
    @DisplayName("Bad: a loop index and accumulated state leak outside their loop")
    void badWideScopeLeaks() {
        BadWideScope bad = new BadWideScope();
        List<Integer> values = Arrays.asList(1, 2, 3);
        assertEquals(6, bad.sum(values));
        // Wide scope: 'i' is declared outside its loop and 'total' lives in the
        // field, both tempting later methods to reuse stale values.
        assertTrue(bad.containsPositive(Arrays.asList(-1, 5)));
        assertFalse(bad.containsPositive(Arrays.asList(-1, -2)));
    }

    @Test
    @DisplayName("Good: variables are declared where used, inside the smallest block")
    void goodNarrowScope() {
        GoodNarrowScope good = new GoodNarrowScope();
        List<Integer> values = Arrays.asList(1, 2, 3);
        assertEquals(6, good.sum(values));
        assertTrue(good.containsPositive(Arrays.asList(-1, 5)));
        assertFalse(good.containsPositive(Arrays.asList(-1, -2)));
    }
}
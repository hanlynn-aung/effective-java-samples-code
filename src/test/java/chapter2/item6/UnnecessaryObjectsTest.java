package chapter2.item6;

import chapter2.item6.bad.BadBoxedSum;
import chapter2.item6.bad.BadRegexMatcher;
import chapter2.item6.good.GoodPrimitiveSum;
import chapter2.item6.good.GoodRegexMatcher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UnnecessaryObjectsTest {

    @Test
    @DisplayName("regex: both matchers agree on correctness")
    void regexMatchersAgree() {
        assertTrue(new BadRegexMatcher().isEmail("han@example.com"));
        assertFalse(new BadRegexMatcher().isEmail("not-an-email"));
        assertTrue(new GoodRegexMatcher().isEmail("han@example.com"));
        assertFalse(new GoodRegexMatcher().isEmail("not-an-email"));
    }

    @Test
    @DisplayName("sum: boxed and primitive sums produce identical results")
    void sumsProduceSameResult() {
        assertEquals(new GoodPrimitiveSum().sum(100),
                new BadBoxedSum().sum(100));
    }

    @Test
    @DisplayName("sum: known value is correct")
    void sumCorrectForKnownInput() {
        assertEquals(4950L, new GoodPrimitiveSum().sum(100));
    }
}
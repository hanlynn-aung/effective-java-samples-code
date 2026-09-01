package chapter10;

import chapter10.bad.BadExceptionControlFlow;
import chapter10.good.GoodConditionCheck;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExceptionUseTest {

    @Test
    @DisplayName("Both: reach the same answer, but the bad version uses exceptions as control flow")
    void bothFindTarget() {
        List<String> values = Arrays.asList("a", "b", "c");
        assertTrue(new BadExceptionControlFlow().contains(values, "b"));
        assertFalse(new BadExceptionControlFlow().contains(values, "z"));
        assertTrue(new GoodConditionCheck().contains(values, "b"));
        assertFalse(new GoodConditionCheck().contains(values, "z"));
    }

    @Test
    @DisplayName("Both: count digits - one throws an exception to end the loop, one uses length()")
    void bothCountDigits() {
        assertEquals(3, new BadExceptionControlFlow().countDigits("a1b2c3"));
        assertEquals(3, new GoodConditionCheck().countDigits("a1b2c3"));
        assertEquals(0, new BadExceptionControlFlow().countDigits("abc"));
        assertEquals(0, new GoodConditionCheck().countDigits("abc"));
    }
}
package chapter8;

import chapter8.bad.BadNullReturn;
import chapter8.good.GoodEmptyReturn;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmptyReturnTest {

    @Test
    @DisplayName("Bad: an empty result returns null, so naive iteration NPEs")
    void badNullBreaksIteration() {
        BadNullReturn bad = new BadNullReturn();
        List<String> empty = bad.find("");
        assertThrows(NullPointerException.class, () -> empty.size());
    }

    @Test
    @DisplayName("Good: an empty result is an empty list - safe to iterate and size")
    void goodEmptyIsSafe() {
        GoodEmptyReturn good = new GoodEmptyReturn();
        List<String> empty = good.find("");
        assertEquals(0, empty.size());
        assertEquals(0, empty.stream().count());
    }

    @Test
    @DisplayName("Both: hits still come back as a populated list")
    void hitsArePopulated() {
        assertEquals(1, new BadNullReturn().find("x").size());
        assertEquals(1, new GoodEmptyReturn().find("x").size());
    }
}
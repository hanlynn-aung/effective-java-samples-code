package chapter6;

import chapter6.bad.BadStatus;
import chapter6.good.GoodStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnumBasicsTest {

    @Test
    @DisplayName("Bad: int constants silently accept garbage integers")
    void badIntConstantsAcceptGarbage() {
        BadStatus status = new BadStatus();
        assertEquals("failed", status.describe(BadStatus.FAILED));
        assertEquals("unknown", status.describe(999));
        assertEquals("unknown", status.describe(-5));
    }

    @Test
    @DisplayName("Good: an enum is a real type enforcing its own membership")
    void goodEnumIsTypeSafe() {
        assertEquals(2, GoodStatus.values().length);
        assertEquals(List.of(GoodStatus.READY, GoodStatus.FAILED),
                Arrays.asList(GoodStatus.values()));
        assertEquals("failed", GoodStatus.FAILED.description());
    }

    @Test
    @DisplayName("Good: values() spans every constant without hand-rolled state")
    void goodEnumIteratesAllConstants() {
        assertTrue(Arrays.stream(GoodStatus.values())
                .allMatch(s -> s.description() != null));
    }
}
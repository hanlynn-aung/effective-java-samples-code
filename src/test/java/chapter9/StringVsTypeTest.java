package chapter9;

import chapter9.bad.BadStringState;
import chapter9.good.GoodTypedEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringVsTypeTest {

    @Test
    @DisplayName("Bad: a magic-string typo silently behaves differently")
    void badStringTypoMisbehaves() {
        BadStringState bad = new BadStringState("READY");
        assertEquals(true, bad.isReady());
        // A caller typos the state; isReady() silently returns false.
        BadStringState typo = new BadStringState("Ready"); // wrong casing
        assertEquals(false, typo.isReady());
    }

    @Test
    @DisplayName("Bad: advance() depends on string literals matching exactly")
    void badAdvanceNeedsExactLiterals() {
        BadStringState bad = new BadStringState("Pending"); // wrong case
        bad.advance();
        assertEquals("Pending", bad.state());
    }

    @Test
    @DisplayName("Good: enum is type-safe - wrong values don't compile, case is irrelevant")
    void goodEnumIsTypeSafe() {
        GoodTypedEnum good = new GoodTypedEnum(GoodTypedEnum.Status.READY);
        assertEquals(true, good.isReady());
        good.advance();
        assertEquals(GoodTypedEnum.Status.READY, good.status());

        GoodTypedEnum pending = new GoodTypedEnum(GoodTypedEnum.Status.PENDING);
        pending.advance();
        assertEquals(GoodTypedEnum.Status.READY, pending.status());
    }
}
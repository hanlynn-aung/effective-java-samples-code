package chapter10;

import chapter10.bad.BadSwallowed;
import chapter10.good.GoodHandle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IgnoreExceptionTest {

    @Test
    @DisplayName("Bad: a swallowed exception hides the failure entirely")
    void badSwallows() {
        BadSwallowed bad = new BadSwallowed();
        // A parse error becomes a bogus -1 that the caller can't distinguish.
        assertEquals(-1, bad.safeParse("not-a-number"));
    }

    @Test
    @DisplayName("Good: the exception is surfaced, wrapped with context")
    void goodSurfaces() {
        GoodHandle good = new GoodHandle();
        assertThrows(IllegalArgumentException.class, () -> good.safeParse("not-a-number"));
    }
}
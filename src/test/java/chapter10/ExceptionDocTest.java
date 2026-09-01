package chapter10;

import chapter10.bad.BadUndocumented;
import chapter10.good.GoodDocExceptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExceptionDocTest {

    @Test
    @DisplayName("Good: the documented @throws (negative year) is honoured by the code")
    void goodDocContractIsHonoured() {
        GoodDocExceptions good = new GoodDocExceptions();
        assertEquals(24, good.twoDigitYear(2024));
        assertThrows(IllegalArgumentException.class, () -> good.twoDigitYear(-1));
    }

    @Test
    @DisplayName("Bad: no @throws - the negative-year failure is undocumented")
    void badHasNoDoc() {
        BadUndocumented bad = new BadUndocumented();
        assertEquals(24, bad.twoDigitYear(2024));
        assertThrows(IllegalArgumentException.class, () -> bad.twoDigitYear(-1));
    }
}
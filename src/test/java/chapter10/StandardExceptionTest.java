package chapter10;

import chapter10.bad.BadInventedException;
import chapter10.good.GoodStandardException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class StandardExceptionTest {

    @Test
    @DisplayName("Bad: a bespoke checked MyException where the standard exceptions fit")
    void badInventedException() throws BadInventedException.MyException {
        BadInventedException bad = new BadInventedException();
        assertThrows(BadInventedException.MyException.class, () -> bad.requiredIndex(null));
        assertThrows(BadInventedException.MyException.class, () -> bad.requiredIndex(""));
    }

    @Test
    @DisplayName("Good: standard exceptions (NPE, IllegalArgumentException) express the failure")
    void goodUsesStandard() {
        GoodStandardException good = new GoodStandardException();
        assertThrows(NullPointerException.class, () -> good.requiredIndex(null));
        assertThrows(IllegalArgumentException.class, () -> good.requiredIndex(""));
    }
}
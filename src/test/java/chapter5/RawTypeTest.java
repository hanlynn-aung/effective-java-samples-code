package chapter5;

import chapter5.bad.BadNumbers;
import chapter5.good.GoodNumbers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RawTypeTest {

    @Test
    @DisplayName("Bad: raw list defers the type error to a runtime cast")
    void badRawListBreaksAtReadTime() {
        BadNumbers numbers = new BadNumbers();
        assertEquals(42, numbers.values().get(0));
        Object bogus = numbers.values().get(1);
        assertThrows(ClassCastException.class,
                () -> { Integer exploded = (Integer) bogus; });
    }

    @Test
    @DisplayName("Good: parameterized list guarantees the element type at compile time")
    void goodParameterizedListIsTypeSafe() {
        GoodNumbers numbers = new GoodNumbers();
        int first = numbers.values().get(0);
        assertEquals(42, first);
    }
}
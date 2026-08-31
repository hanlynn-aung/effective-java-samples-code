package chapter8;

import chapter8.bad.BadVarargs;
import chapter8.good.GoodVarargs;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VarargsTest {

    @Test
    @DisplayName("Bad: min() of zero args throws ArrayIndexOutOfBoundsException")
    void badZeroArgMinThrows() {
        BadVarargs bad = new BadVarargs();
        assertEquals(0, bad.sum());
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> bad.min());
    }

    @Test
    @DisplayName("Good: min() keeps a required first parameter, so zero args can't crash")
    void goodZeroArgMinIsPrevented() {
        GoodVarargs good = new GoodVarargs();
        assertEquals(2, good.min(2, 5, 3));
        assertEquals(4, good.min(4));
        assertEquals(0, good.sum());
    }

    @Test
    @DisplayName("Both: sum() with zero args is a legitimate, well-defined varargs use")
    void sumIsFineEmpty() {
        assertEquals(0, new BadVarargs().sum());
        assertEquals(0, new GoodVarargs().sum());
    }
}
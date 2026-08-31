package chapter9;

import chapter9.bad.BadBoxedTrap;
import chapter9.good.GoodPrimitives;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BoxedPrimitiveTest {

    @Test
    @DisplayName("Bad: == on boxed Integers is identity, not value - surprising for small cached values")
    void badBoxedEqualityByIdentity() {
        BadBoxedTrap bad = new BadBoxedTrap();
        // -128..127 are cached, so these happen to be equal; larger values are not.
        assertEquals(true, bad.sameRank(100, 100));
        assertNotEquals(true, bad.sameRank(1000, 1000));
    }

    @Test
    @DisplayName("Bad: unboxing a null Integer throws NPE")
    void badUnboxingNullThrows() {
        BadBoxedTrap bad = new BadBoxedTrap();
        Map<String, Integer> map = new HashMap<>();
        map.put("a", 5);
        map.put("b", null);
        assertThrows(NullPointerException.class, () -> bad.maxUnboxed(map));
    }

    @Test
    @DisplayName("Good: primitives compare by value, avoid null and identity traps")
    void goodPrimitivesAreValueSane() {
        GoodPrimitives good = new GoodPrimitives();
        assertEquals(true, good.sameRank(1000, 1000));
        assertEquals(9L, good.sum(4L, 5L));
        assertEquals(0, good.compare(3L, 3L));
        assertEquals(-1, good.compare(3L, 9L));
    }

    @Test
    @DisplayName("Good: with primitives, a null element is impossible and max() is safe")
    void goodMaxIsSafeWithoutNull() {
        GoodPrimitives good = new GoodPrimitives();
        Map<String, Integer> map = new HashMap<>();
        map.put("a", 5);
        map.put("b", 12);
        assertEquals(12, good.maxUnboxed(map));
    }

    @Test
    @DisplayName("Both: countAbove works identically (unboxing happens but there is no null)")
    void bothCountAbove() {
        Map<String, Integer> map = new HashMap<>();
        map.put("a", 5);
        map.put("b", 12);
        map.put("c", 3);
        assertEquals(2, new BadBoxedTrap().countAbove(map, 4));
        assertEquals(2, new GoodPrimitives().countAbove(map, 4));
    }
}
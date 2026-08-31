package chapter5;

import chapter5.bad.BadCovariantArray;
import chapter5.good.GoodNumberList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ListVsArrayTest {

    @Test
    @DisplayName("Bad: a Long[] accepts a String through covariance and explodes on store")
    void badCovariantArrayAcceptsHeapPollution() {
        BadCovariantArray bad = new BadCovariantArray();
        assertThrows(ArrayStoreException.class, bad::covariantTrap);
    }

    @Test
    @DisplayName("Bad: reified array types can hide mixed types that fail on cast, not on write")
    void badArrayLosesElementTypeKnowledge() {
        BadCovariantArray bad = new BadCovariantArray();
        // The bag succeeds; the NUMBER contract is only enforced at read time.
        Object[] bag = bad.sneakyMixedBag();
        assertEquals("one", bag[0]);
        assertThrows(ClassCastException.class,
                () -> ((Number) bag[0]).intValue());
    }

    @Test
    @DisplayName("Good: generic list remembers its element type for the whole pipeline")
    void goodListPreservesTypeInformation() {
        GoodNumberList good = new GoodNumberList();
        List<Number> numbers = good.mixedNumbers();
        assertEquals(42, numbers.get(0).intValue());
        assertEquals(3.14, numbers.get(1).doubleValue());
    }
}
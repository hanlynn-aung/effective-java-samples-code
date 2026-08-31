package chapter3;

import chapter3.bad.BadInconsistentPerson;
import chapter3.bad.BadSubtractionComparator;
import chapter3.good.GoodComparablePerson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ComparableTest {

    @Test
    @DisplayName("Bad: subtraction-based compare overflows")
    void badSubtractionOverflows() {
        int result = new BadSubtractionComparator().compare(
                new BadSubtractionComparator.Item(Integer.MAX_VALUE),
                new BadSubtractionComparator.Item(-1));
        assertTrue(result < 0, "claims MAX_VALUE < -1");
    }

    @Test
    @DisplayName("Bad: compareTo inconsistent with equals collapses TreeSet entries")
    void badInconsistencyCollapsesTreeSet() {
        TreeSet<BadInconsistentPerson> set = new TreeSet<>();
        set.add(new BadInconsistentPerson("Han", 10));
        set.add(new BadInconsistentPerson("Han", 20));
        assertFalse(new BadInconsistentPerson("Han", 10)
                .equals(new BadInconsistentPerson("Han", 20)));
        assertEquals(1, set.size());
    }

    @Test
    @DisplayName("Good: consistent compareTo keeps distinct entries")
    void goodConsistencyKeepsEntries() {
        TreeSet<GoodComparablePerson> set = new TreeSet<>();
        set.add(new GoodComparablePerson("Han", 10));
        set.add(new GoodComparablePerson("Han", 20));
        assertEquals(2, set.size());
    }

    @Test
    @DisplayName("Good: compareTo returns 0 exactly when equals returns true")
    void goodCompareToMatchesEquals() {
        GoodComparablePerson a = new GoodComparablePerson("Han", 10);
        GoodComparablePerson b = new GoodComparablePerson("Han", 10);
        assertEquals(0, a.compareTo(b));
        assertTrue(a.equals(b));
    }

    @Test
    @DisplayName("Good: ordering sorts by score without overflow")
    void goodOrdersByScore() {
        List<GoodComparablePerson> sorted = new TreeSet<>(List.of(
                new GoodComparablePerson("a", 5),
                new GoodComparablePerson("b", 1),
                new GoodComparablePerson("c", 3))).stream().toList();
        assertEquals(List.of(1, 3, 5),
                sorted.stream().map(GoodComparablePerson::score).toList());
    }
}
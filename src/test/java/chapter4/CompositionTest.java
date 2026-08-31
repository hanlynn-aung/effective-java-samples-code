package chapter4;

import chapter4.bad.BadInstrumentedHashSet;
import chapter4.bad.BadStack;
import chapter4.good.GoodInstrumentedSet;
import chapter4.good.GoodStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompositionTest {

    @Test
    @DisplayName("Bad: HashSet inheritance double-counts addAll")
    void badInstrumentedHashSetDoubleCounts() {
        BadInstrumentedHashSet<String> set = new BadInstrumentedHashSet<>();
        set.addAll(List.of("a", "b", "c"));
        assertEquals(3, set.size());
        assertEquals(6, set.getAddCount());
    }

    @Test
    @DisplayName("Good: composition delegates but counts only real changes")
    void goodInstrumentedSetCountsOnlyNewElements() {
        GoodInstrumentedSet<String> set = GoodInstrumentedSet.of();
        set.addAll(List.of("a", "b", "c"));
        assertEquals(3, set.size());
        assertEquals(3, set.getAddCount());

        set.add("a");
        set.add("d");
        assertEquals(4, set.size());
        assertEquals(4, set.getAddCount());
    }

    @Test
    @DisplayName("Bad: a Stack that is an ArrayList leaks non-stack operations")
    void badStackLeaksCollectionOperations() {
        BadStack stack = new BadStack();
        stack.add("a");
        stack.add("b");
        stack.add(0, "intruder");
        assertEquals("b", stack.pop());
        assertEquals("intruder", stack.get(0));
        assertTrue(stack.contains("intruder"));
    }

    @Test
    @DisplayName("Good: composition exposes only the stack contract")
    void goodStackExposesOnlyStackContract() {
        GoodStack stack = new GoodStack();
        stack.push("a");
        stack.push("b");
        assertEquals("b", stack.pop());
        assertEquals("a", stack.pop());
        assertTrue(stack.isEmpty());
    }
}
package chapter5;

import chapter5.bad.BadSetHelpers;
import chapter5.good.GoodSetHelpers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GenericMethodTest {

    @Test
    @DisplayName("Bad: raw union loses the element type and steals type safety")
    void badRawUnionNeedsCasts() {
        BadSetHelpers bad = new BadSetHelpers();
        Set<String> words = Set.of("apple");
        Set<Integer> numbers = Set.of(42);

        @SuppressWarnings("unchecked")
        Set<String> merged = (Set<String>) bad.union(words, numbers);
        assertEquals(2, merged.size());
        assertThrows(ClassCastException.class,
                () -> { for (String unused : merged) { } });
    }

    @Test
    @DisplayName("Good: generic union is typed with zero casts")
    void goodUnionIsTyped() {
        Set<String> result = GoodSetHelpers.union(Set.of("a", "b"), Set.of("b", "c"));
        assertEquals(Set.of("a", "b", "c"), result);
    }

    @Test
    @DisplayName("Good: recursive bound max works on natural orders")
    void goodMaxUsesRecursiveBound() {
        assertEquals("kiwi", GoodSetHelpers.max(Set.of("apple", "kiwi", "fig")));
        assertEquals(42, GoodSetHelpers.max(Set.of(3, 7, 42)));
    }
}
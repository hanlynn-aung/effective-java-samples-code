package chapter9;

import chapter9.bad.BadIndexLoop;
import chapter9.good.GoodForEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoopTest {

    @Test
    @DisplayName("Both: count empty strings in a List - for-each has no index machinery")
    void bothCountEmptyInList() {
        List<String> names = Arrays.asList("", "a", "", "bb");
        assertEquals(2, new BadIndexLoop(names).countEmpty());
        assertEquals(2, new GoodForEach(names).countEmpty());
    }

    @Test
    @DisplayName("Good: for-each works over any Iterable - a Set has no get(i), yet the loop is fine")
    void goodForEachWorksOverSet() {
        Set<String> names = new HashSet<>(Arrays.asList("", "a", ""));
        assertEquals(1, new GoodForEach(names).countEmpty());
        // BadIndexLoop cannot even take a Set: Sets have no indexed get().
    }
}
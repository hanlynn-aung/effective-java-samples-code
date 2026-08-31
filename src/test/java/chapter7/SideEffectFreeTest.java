package chapter7;

import chapter7.bad.BadStatefulCollect;
import chapter7.good.GoodCollectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SideEffectFreeTest {

    @Test
    @DisplayName("Both approaches uppercase the top N words identically")
    void bothProduceSameResult() {
        String[] words = {"one", "two", "three", "four"};
        List<String> bad = new BadStatefulCollect().uppercaseTopWords(words, 3);
        List<String> good = new GoodCollectors().uppercaseTopWords(words, 3);
        assertEquals(List.of("ONE", "TWO", "THREE"), bad);
        assertEquals(bad, good);
    }

    @Test
    @DisplayName("Bad: order depends on the side-effecting forEach into an external bucket")
    void badDependsOnEncounterOrder() {
        // Re-processing the same words keeps a stable order only because the
        // stream is sequential; parallelism would scramble the external list.
        String[] words = {"a", "b", "c"};
        assertEquals(List.of("A", "B", "C"),
                new BadStatefulCollect().uppercaseTopWords(words, 3));
    }
}
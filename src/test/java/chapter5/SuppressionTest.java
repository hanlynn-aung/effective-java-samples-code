package chapter5;

import chapter5.bad.BadWholeClassSuppression;
import chapter5.good.GoodScopedSuppression;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SuppressionTest {

    @Test
    @DisplayName("Bad: class-wide suppression hides a raw cast that is not safe")
    void badClassWideSuppressionMasqueradesUnsafety() {
        BadWholeClassSuppression hidden = new BadWholeClassSuppression();
        hidden.add(42);
        hidden.add("not an integer");
        assertThrows(ClassCastException.class,
                () -> { Integer exploded = hidden.asIntegers().get(1); });
    }

    @Test
    @DisplayName("Good: the warning is eliminated by redesign, not by suppression")
    void goodRedesignNeedsNoSuppression() {
        GoodScopedSuppression helper = new GoodScopedSuppression();
        List<Integer> integers = helper.asIntegers(List.of("1", "2", "3"));
        assertEquals(List.of(1, 2, 3), integers);
        assertEquals(3, integers.size());
    }
}
package chapter5;

import chapter5.bad.BadHeapPollution;
import chapter5.good.GoodSafeVarargs;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SafeVarargsTest {

    @Test
    @DisplayName("Bad: leaking a generic varargs array lets a caller poison it")
    void badVarargsArrayCanBePoisoned() {
        assertThrows(ClassCastException.class,
                () -> BadHeapPollution.dangerous(List.of("a")));
    }

    @Test
    @DisplayName("Good: a varargs method that never leaks its array is safe")
    void goodVarargsFlattenIsSafe() {
        List<String> flat = GoodSafeVarargs.flatten(List.of("a", "b"), List.of("c"));
        assertEquals(List.of("a", "b", "c"), flat);
    }

    @Test
    @DisplayName("Good: the safe method is annotated @SafeVarargs")
    void goodMethodCarriesSafeVarargs() throws Exception {
        Method flatten = GoodSafeVarargs.class.getMethod("flatten", List[].class);
        assertTrue(flatten.isAnnotationPresent(SafeVarargs.class));
    }
}
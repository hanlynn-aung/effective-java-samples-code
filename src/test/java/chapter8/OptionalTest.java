package chapter8;

import chapter8.bad.BadOptionalOveruse;
import chapter8.good.GoodOptional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OptionalTest {

    @Test
    @DisplayName("Bad: Optional wraps a collection (should be returned directly) and a primitive")
    void badWrapsCollectionAndPrimitive() {
        BadOptionalOveruse bad = new BadOptionalOveruse();
        Optional<List<String>> words = bad.words();
        assertTrue(words.isPresent() && words.get().size() == 2);

        // Primitive handling forces boxing and an extra Optional layer.
        Optional<Double> price = bad.lastPrice();
        assertTrue(price.isPresent() && price.get() == 12.50);
    }

    @Test
    @DisplayName("Good: collections return directly; primitives use the primitive Optional")
    void goodRightSized() {
        GoodOptional good = new GoodOptional();
        assertEquals(2, good.words().size());

        OptionalDouble price = good.lastPrice();
        assertTrue(price.isPresent() && price.getAsDouble() == 12.50);
    }

    @Test
    @DisplayName("Good: Optional is for a genuinely absent value, used with orElse/orElseGet")
    void goodOptionalUseCases() {
        GoodOptional good = new GoodOptional();
        assertEquals("bc", good.middle("abcd").orElse("none"));
        assertEquals("none", good.middle("").orElseGet(() -> "none"));
        assertEquals("none", good.middle(null).orElse("none"));
    }
}
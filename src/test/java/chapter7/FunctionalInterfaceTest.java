package chapter7;

import chapter7.bad.BadInventedEvent;
import chapter7.good.GoodUseStandard;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FunctionalInterfaceTest {

    @Test
    @DisplayName("Bad: a bespoke single-method interface duplicates BiConsumer")
    void badInventedInterfaceDuplicatesStandard() {
        BadInventedEvent event = new BadInventedEvent();
        List<Double> received = new ArrayList<>();
        event.setListener(price -> received.add(price));
        event.onPrice(3.5);
        assertEquals(List.of(3.5), received);
    }

    @Test
    @DisplayName("Good: the standard BiConsumer is directly usable as the callback")
    void goodUsesStandardBiConsumer() {
        GoodUseStandard good = new GoodUseStandard();
        List<String> seen = new ArrayList<>();
        BiConsumer<String, Double> reporter = (ticker, price) ->
                seen.add(ticker + "=" + price);
        good.onPrice(reporter, "AAPL", 3.5);
        assertEquals(List.of("AAPL=3.5"), seen);
    }

    @Test
    @DisplayName("Good: standard predicate/function slots take lambdas with no custom types")
    void goodStandardInterfacesAreDropIn() {
        GoodUseStandard good = new GoodUseStandard();
        Predicate<String> shortWord = good.shorterThan(4);
        assertTrue(shortWord.test("hi"));
        Function<Integer, String> roman = good.asRoman();
        assertEquals("I", roman.apply(1));
    }
}
package chapter7;

import chapter7.bad.BadFormatter;
import chapter7.good.GoodFormatter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LambdaTest {

    @Test
    @DisplayName("Both the anonymous class and the lambda produce the same result")
    void bothApproachesAgree() {
        BadFormatter bad = new BadFormatter();
        GoodFormatter good = new GoodFormatter();
        String input = "  HeLLo  ";
        assertEquals(bad.format(input), good.format(input));
    }

    @Test
    @DisplayName("Bad: an anonymous Function class carries verbose boilerplate")
    void badAnonymousClassIsVerbose() {
        Function<String, String> fn = new java.util.function.Function<>() {
            @Override
            public String apply(String s) {
                return s.trim();
            }
        };
        assertEquals("x", fn.apply("  x  "));
    }

    @Test
    @DisplayName("Good: a lambda collapses to one clean expression and holds no accidental state")
    void goodLambdaIsConciseAndStateFree() {
        Function<String, String> fn = String::trim;
        assertEquals("x", fn.apply("  x  "));
    }
}
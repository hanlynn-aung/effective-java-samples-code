package chapter7;

import chapter7.bad.BadVerbose;
import chapter7.good.GoodConcise;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MethodReferenceTest {

    @Test
    @DisplayName("A method reference and its equivalent lambda agree")
    void methodReferenceMatchesLambda() {
        GoodConcise concise = new GoodConcise();
        Function<String, Integer> byRef = concise.lengthOf();
        Function<String, Integer> byLambda = String::length;
        assertEquals(byLambda.apply("hello"), byRef.apply("hello"));
        assertEquals(5, byRef.apply("hello"));
    }

    @Test
    @DisplayName("Good: method references shrink the same logic to near-point-free form")
    void goodUsesMethodReferences() {
        GoodConcise concise = new GoodConcise();
        String[] words = {"a", "bcd", "very-long", "hi"};
        assertEquals(3, concise.countShorts(words));
        assertEquals(List.of("a", "bcd", "very-long", "hi"), concise.toList(words));
    }

    @Test
    @DisplayName("Bad: the same counting logic is a maze of lambdas and casts")
    void badIsVerbose() {
        BadVerbose bad = new BadVerbose();
        String[] words = {"a", "bcd", "very-long", "hi"};
        assertEquals(3, bad.countShorts(words));
    }
}
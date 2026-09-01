package chapter9;

import chapter9.bad.BadReflective;
import chapter9.good.GoodInterfaceInvocation;
import chapter9.good.Greeter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReflectionTest {

    @Test
    @DisplayName("Good: compile-time interface invocation needs no reflection and is type-checked")
    void goodUsesInterface() {
        Greeter greeter = new GoodInterfaceInvocation.FriendlyGreeter();
        GoodInterfaceInvocation invoker = new GoodInterfaceInvocation(greeter);
        assertEquals("Hello, Ada!", invoker.buildGreeting("Ada"));
    }

    @Test
    @DisplayName("Bad: a wrong class name fails only at runtime via reflection")
    void badReflectionFailsAtRuntime() {
        BadReflective bad = new BadReflective();
        // A typo in the class name is only discovered at runtime.
        assertThrows(IllegalArgumentException.class,
                () -> bad.buildGreeting("chapter9.good.FriendlyGreter", "Ada"));
    }
}
package chapter5;

import chapter5.bad.BadObjectStack;
import chapter5.good.GoodGenericStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GenericTypeTest {

    @Test
    @DisplayName("Bad: an Object stack forces callers into casts that can break")
    void badObjectStackNeedsCasts() {
        BadObjectStack stack = new BadObjectStack();
        stack.push("a");
        stack.push(7);
        Object top = stack.pop();
        assertThrows(ClassCastException.class, () -> { String doomed = (String) top; });
    }

    @Test
    @DisplayName("Good: a generic stack needs no casts and cannot mix element types")
    void goodGenericStackIsCovariantFree() {
        GoodGenericStack<String> stack = new GoodGenericStack<>();
        stack.push("a");
        stack.push("b");
        String top = stack.pop();
        assertEquals("b", top);
        assertEquals("a", stack.pop());
        assertTrue(stack.isEmpty());
    }

    @Test
    @DisplayName("Good: popping an empty stack fails fast instead of returning garbage")
    void goodGenericStackFailsFastOnEmpty() {
        GoodGenericStack<Integer> stack = new GoodGenericStack<>();
        assertThrows(IllegalStateException.class, stack::pop);
    }
}
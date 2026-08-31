package chapter2.item7;

import chapter2.item7.bad.BadStack;
import chapter2.item7.good.GoodStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ObsoleteReferencesTest {

    private static Object elementAt(Object stack, int index) throws Exception {
        Field f = stack.getClass().getDeclaredField("elements");
        f.setAccessible(true);
        Object[] elements = (Object[]) f.get(stack);
        return elements[index];
    }

    @Test
    @DisplayName("Bad: a popped reference is retained in the backing array")
    void badStackRetainsPoppedReference() throws Exception {
        BadStack stack = new BadStack();
        stack.push("a");
        stack.push("b");
        stack.pop();
        assertEquals("b", elementAt(stack, 1));
    }

    @Test
    @DisplayName("Good: pop clears the backing slot so the object can be GC'd")
    void goodStackClearsPoppedSlot() throws Exception {
        GoodStack stack = new GoodStack();
        stack.push("a");
        stack.push("b");
        stack.pop();
        assertNull(elementAt(stack, 1));
    }
}
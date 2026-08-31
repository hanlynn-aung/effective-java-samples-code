package chapter4;

import chapter4.bad.BadMutableTime;
import chapter4.good.GoodTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImmutabilityTest {

    @Test
    @DisplayName("Bad: setters mutate the object after construction")
    void badSetttersMutateExistingObject() {
        BadMutableTime time = new BadMutableTime(10, 30);
        time.setMinute(45);
        assertEquals("BadMutableTime[10:45]", time.toString());
    }

    @Test
    @DisplayName("Good: withHour returns a new object, original unchanged")
    void goodWithReturnsNewInstance() {
        GoodTime original = new GoodTime(10, 30);
        GoodTime shifted = original.withHour(22);
        assertNotSame(original, shifted);
        assertEquals(10, original.hour());
        assertEquals(30, original.minute());
        assertEquals(22, shifted.hour());
        assertEquals(30, shifted.minute());
    }

    @Test
    @DisplayName("Good: constructor validates to keep invariants forever")
    void goodConstructorValidatesInvariants() {
        assertThrows(IllegalArgumentException.class, () -> new GoodTime(24, 0));
        assertThrows(IllegalArgumentException.class, () -> new GoodTime(10, -1));
    }

    @Test
    @DisplayName("Good: fields are private and final, class is final")
    void goodClassIsEffectivelyImmutableByDeclaration() throws Exception {
        assertTrue(Modifier.isFinal(GoodTime.class.getModifiers()));
        for (Field field : GoodTime.class.getDeclaredFields()) {
            assertTrue(Modifier.isFinal(field.getModifiers()),
                    field.getName() + " must be final");
            assertTrue(Modifier.isPrivate(field.getModifiers()),
                    field.getName() + " must be private");
        }
    }
}
package chapter4;

import chapter4.bad.BadExplodingCounter;
import chapter4.bad.FragileBaseCounter;
import chapter4.good.GoodFinalCounter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InheritanceFragilityTest {

    @Test
    @DisplayName("Bad: constructing the subclass explodes via the super constructor")
    void badSubclassConstructorExplodes() {
        assertThrows(NullPointerException.class, BadExplodingCounter::new);
    }

    @Test
    @DisplayName("Good: a final class whose constructor calls only its own non-overridable methods")
    void goodFinalCounterWorksAcrossConstructionAndUse() {
        GoodFinalCounter counter = new GoodFinalCounter();
        counter.add(2);
        counter.add(3);
        assertEquals(5, counter.count());
    }

    @Test
    @DisplayName("Good: the safe class is explicitly final, so nothing can be overridden")
    void goodClassRejectsExtensionAtCompileTime() {
        assertTrue(Modifier.isFinal(GoodFinalCounter.class.getModifiers()));
        assertTrue(Modifier.isPublic(FragileBaseCounter.class.getModifiers()));
    }
}
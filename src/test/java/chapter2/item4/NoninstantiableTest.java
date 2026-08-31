package chapter2.item4;

import chapter2.item4.bad.BadUtilityClass;
import chapter2.item4.good.GoodUtilityClass;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NoninstantiableTest {

    @Test
    @DisplayName("Bad: no private ctor means the class is accidentally instantiable")
    void badUtilityClassIsInstantiable() {
        assertEquals("hello", new BadUtilityClass().normalize(" hello "));
    }

    @Test
    @DisplayName("Good: private ctor refuses even reflective instantiation")
    void goodUtilityClassRefusesReflectiveInstantiation() throws Exception {
        Constructor<GoodUtilityClass> ctor =
                GoodUtilityClass.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        InvocationTargetException ex =
                assertThrows(InvocationTargetException.class, ctor::newInstance);
        assertInstanceOf(AssertionError.class, ex.getCause());
    }
}
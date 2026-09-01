package chapter10;

import chapter10.bad.BadLeakyException;
import chapter10.good.GoodTranslation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExceptionTranslationTest {

    @Test
    @DisplayName("Bad: a low-level SQL exception leaks out of a config-loading abstraction")
    void badLeaksLowLevel() {
        BadLeakyException bad = new BadLeakyException();
        RuntimeException wrapper = assertThrows(RuntimeException.class, () -> bad.loadConfig("x"));
        assertTrue(wrapper.getCause() instanceof java.sql.SQLException);
    }

    @Test
    @DisplayName("Good: the low-level cause is translated to an abstraction-appropriate exception")
    void goodTranslates() {
        GoodTranslation good = new GoodTranslation();
        GoodTranslation.ConfigLoaderException e = assertThrows(
                GoodTranslation.ConfigLoaderException.class, () -> good.loadConfig("x"));
        assertTrue(e.getMessage().contains("config"));
        assertEquals(java.sql.SQLException.class, e.getCause().getClass());
    }
}
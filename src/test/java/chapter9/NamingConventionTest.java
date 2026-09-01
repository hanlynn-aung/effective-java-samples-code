package chapter9;

import chapter9.bad.BadNaming;
import chapter9.good.GoodNaming;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NamingConventionTest {

    @Test
    @DisplayName("Bad: single-letter and opaque names('n','x','gv','chk') are unreadable")
    void badNamesObscureMeaning() {
        BadNaming bad = new BadNaming(100);
        assertEquals(100, bad.gv());
        bad.st(120);
        assertTrue(bad.chk());
    }

    @Test
    @DisplayName("Good: descriptive, conventional names read clearly and validate")
    void goodNamesAreClear() {
        GoodNaming good = new GoodNaming(100);
        good.setValue(120);
        assertTrue(good.isAtOrAboveLimit());
        good.setValue(50);
        assertFalse(good.isAtOrAboveLimit());
        assertThrows(IllegalArgumentException.class, () -> new GoodNaming(-1));
    }
}
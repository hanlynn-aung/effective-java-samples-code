package chapter11;

import chapter11.bad.BadLazyInit;
import chapter11.good.GoodLazyHolder;
import chapter11.good.GoodLazyInit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class LazyInitTest {

    @Test
    @DisplayName("Good: double-checked locking initializes once and returns the same instance")
    void goodDoubleChecked() {
        GoodLazyInit good = new GoodLazyInit();
        Map<String, String> a = good.getHeavy();
        Map<String, String> b = good.getHeavy();
        assertSame(a, b);
        assertEquals("value", a.get("expensive"));
    }

    @Test
    @DisplayName("Good: the holder-class idiom is a correct lazy singleton")
    void goodHolder() {
        GoodLazyHolder a = GoodLazyHolder.getInstance();
        GoodLazyHolder b = GoodLazyHolder.getInstance();
        assertSame(a, b);
        assertEquals("singleton", a.name());
    }

    @Test
    @DisplayName("Bad: the unsynchronized lazy field at least returns a usable map single-threaded")
    void badUnsyncLazyWorksSingleThreaded() {
        BadLazyInit bad = new BadLazyInit();
        Map<String, String> a = bad.getHeavy();
        Map<String, String> b = bad.getHeavy();
        assertNotNull(a);
        assertNotNull(b);
        assertEquals("value", a.get("expensive"));
    }
}
package chapter9;

import chapter9.bad.BadAlwaysNative;
import chapter9.good.GoodJavaFirst;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NativeTest {

    @Test
    @DisplayName("Bad: reaching for native code where the JDK already has it")
    void badReachesForNative() {
        BadAlwaysNative bad = new BadAlwaysNative();
        assertThrows(UnsupportedOperationException.class, () -> bad.currentTimeNative());
        assertThrows(UnsupportedOperationException.class, () -> bad.uppercaseNative("x"));
    }

    @Test
    @DisplayName("Good: pure Java covers the same ground with no JNI risk")
    void goodUsesJava() {
        GoodJavaFirst good = new GoodJavaFirst();
        assertEquals("ADA", good.uppercaseJava("ada"));
        assertTrue(good.currentTimeJava() > 0);
    }
}
package chapter1;

import chapter1.bad.BadConnectionFactory;
import chapter1.good.GoodCachedConnections;
import chapter1.good.GoodConnectionFactory;
import chapter1.good.GoodTypedConnections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StaticFactoryTest {

    @Test
    @DisplayName("Bad: public constructor accepts null address")
    void badAllowsNullAddress() {
        BadConnectionFactory.Connection connection =
                new BadConnectionFactory().open(null);
        assertEquals(null, connection.address());
    }

    @Test
    @DisplayName("Bad: every open() creates a distinct instance, no reuse")
    void badCreatesDistinctInstances() {
        BadConnectionFactory.Connection a =
                new BadConnectionFactory().open("db://x");
        BadConnectionFactory.Connection b =
                new BadConnectionFactory().open("db://x");
        assertNotSame(a, b);
    }

    @Test
    @DisplayName("Good: named factory rejects null immediately")
    void goodRejectsNull() {
        GoodConnectionFactory factory = new GoodConnectionFactory();
        assertThrows(NullPointerException.class,
                () -> factory.open(null));
    }

    @Test
    @DisplayName("Good: named factory returns a usable connection")
    void goodReturnsConnection() {
        GoodConnectionFactory factory = new GoodConnectionFactory();
        assertEquals("db://x", factory.open("db://x").address());
    }

    @Test
    @DisplayName("Good: cached factory reuses instances (instance control)")
    void cachedReturnsSameInstance() {
        assertSame(GoodCachedConnections.to("db://x"),
                GoodCachedConnections.to("db://x"));
    }

    @Test
    @DisplayName("Good: cached factory still validates its input")
    void cachedRejectsNull() {
        assertThrows(NullPointerException.class,
                () -> GoodCachedConnections.to(null));
    }

    @Test
    @DisplayName("Good: typed factory returns different subtypes on demand")
    void typedReturnsRequestedSubtype() {
        GoodTypedConnections.Connection plain =
                GoodTypedConnections.plain("db://x");
        GoodTypedConnections.Connection secure =
                GoodTypedConnections.secure("https://x");
        assertFalse(plain.secure());
        assertTrue(secure.secure());
    }
}
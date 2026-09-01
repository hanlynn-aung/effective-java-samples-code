package chapter12;

import chapter12.bad.BadDirectSerialization;
import chapter12.good.GoodSerializationProxy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProxyRoundTripTest {

    @Test
    @DisplayName("Good: deserializing through the proxy rebuilds a validated Period via its constructor")
    void goodProxyRoundTrips() throws Exception {
        GoodSerializationProxy v = new GoodSerializationProxy(new Date(1_000L), new Date(9_000L));
        byte[] bytes;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(v);
            bytes = bos.toByteArray();
        }
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            GoodSerializationProxy back = (GoodSerializationProxy) in.readObject();
            assertEquals(8_000L, back.durationMillis());
        }
    }

    @Test
    @DisplayName("Good: the proxy rejects a stream whose internals violate the invariant")
    void goodProxyRejectsInvalidStream() throws Exception {
        GoodSerializationProxy v = new GoodSerializationProxy(new Date(1_000L), new Date(9_000L));
        // Corrupt the internal representation, like an attacker's crafted stream.
        Field s = GoodSerializationProxy.class.getDeclaredField("start");
        Field e = GoodSerializationProxy.class.getDeclaredField("end");
        s.setAccessible(true);
        e.setAccessible(true);
        s.set(v, new Date(9_000L));
        e.set(v, new Date(1_000L));
        byte[] bytes;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(v);
            bytes = bos.toByteArray();
        }
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            assertThrows(InvalidObjectException.class, in::readObject);
        }
    }

    @Test
    @DisplayName("Bad: direct serialization of the class stores the mutable internal representation")
    void badDirectSerialization() throws Exception {
        BadDirectSerialization v = new BadDirectSerialization(new Date(1_000L), new Date(9_000L));
        byte[] bytes;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(v);
            bytes = bos.toByteArray();
        }
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            BadDirectSerialization back = (BadDirectSerialization) in.readObject();
            assertTrue(back.durationMillis() == 8_000L);
        }
    }
}
package chapter12;

import chapter12.bad.BadUnvalidatedReadObject;
import chapter12.good.GoodDefensiveReadObject;
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

class DefensiveReadObjectTest {

    @Test
    @DisplayName("Bad: getters expose internal mutable Date objects - caller can corrupt the instance")
    void badGettersLeakInternalMutables() {
        BadUnvalidatedReadObject v = new BadUnvalidatedReadObject(new Date(1_000L), new Date(9_000L));
        long before = v.durationMillis();
        // Mutate the returned start from the outside.
        v.start().setTime(99_000L);
        assertTrue(v.durationMillis() < before,
                "mutating a leaked internal Date corrupted the value range");
    }

    @Test
    @DisplayName("Good: getters return copies, so external mutation cannot corrupt the instance")
    void goodGettersProtectInternals() {
        GoodDefensiveReadObject v = new GoodDefensiveReadObject(new Date(1_000L), new Date(9_000L));
        v.start().setTime(99_000L);
        assertEquals(8_000L, v.durationMillis(),
                "mutating a returned copy must not affect the instance");
    }

    @Test
    @DisplayName("Bad: default readObject accepts a crafted stream that violates the invariant")
    void badAcceptsInvalidStream() throws Exception {
        BadUnvalidatedReadObject v = new BadUnvalidatedReadObject(new Date(1_000L), new Date(9_000L));
        corruptOrdering(v); // make the internal start AFTER end, like an attacker's stream
        byte[] bytes = serialize(v);
        // The bad readObject has no validation: it deserializes an impossible object.
        BadUnvalidatedReadObject back = (BadUnvalidatedReadObject) deserialize(bytes);
        assertTrue(back.durationMillis() < 0, "crafted invalid period was accepted");
    }

    @Test
    @DisplayName("Good: defensive readObject rejects a crafted stream that violates the invariant")
    void goodRejectsInvalidStream() throws Exception {
        GoodDefensiveReadObject v = new GoodDefensiveReadObject(new Date(1_000L), new Date(9_000L));
        corruptOrdering(v); // simulate an attacker's out-of-order stream
        byte[] bytes = serialize(v);
        assertThrows(InvalidObjectException.class, () -> deserialize(bytes));
    }

    private static void corruptOrdering(Object target) throws Exception {
        Field s = target.getClass().getDeclaredField("start");
        Field e = target.getClass().getDeclaredField("end");
        s.setAccessible(true);
        e.setAccessible(true);
        Date ss = (Date) s.get(target);
        Date ee = (Date) e.get(target);
        s.set(target, new Date(ee.getTime()));
        e.set(target, new Date(ss.getTime()));
    }

    private static byte[] serialize(Object o) throws Exception {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(o);
            return bos.toByteArray();
        }
    }

    private static Object deserialize(byte[] bytes) throws Exception {
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return in.readObject();
        }
    }
}
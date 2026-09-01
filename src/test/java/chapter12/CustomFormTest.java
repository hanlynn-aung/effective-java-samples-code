package chapter12;

import chapter12.bad.BadDefaultSerializedForm;
import chapter12.good.GoodCustomSerializedForm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomFormTest {

    @Test
    @DisplayName("Both: a valid range round-trips unchanged")
    void bothRoundTrip() throws Exception {
        Date start = new Date(1_000L);
        Date end = new Date(9_000L);

        byte[] badBytes = serialize(new BadDefaultSerializedForm(start, end));
        BadDefaultSerializedForm badBack =
                (BadDefaultSerializedForm) deserialize(badBytes);

        byte[] goodBytes = serialize(new GoodCustomSerializedForm(start, end));
        GoodCustomSerializedForm goodBack =
                (GoodCustomSerializedForm) deserialize(goodBytes);

        assertEquals(8_000L, badBack.end().getTime() - badBack.start().getTime());
        assertEquals(8_000L, goodBack.durationMillis());
    }

    @Test
    @DisplayName("Good: the custom form writes far fewer bytes than the default form")
    void goodFormIsCompact() throws Exception {
        Date start = new Date(1_000L);
        Date end = new Date(9_000L);
        byte[] badBytes = serialize(new BadDefaultSerializedForm(start, end));
        byte[] goodBytes = serialize(new GoodCustomSerializedForm(start, end));
        // Custom form writes just two longs; default form writes two full Date
        // objects (larger). Good form must be smaller.
        assertTrue(goodBytes.length < badBytes.length,
                "custom form (" + goodBytes.length + ") should be smaller than default (" + badBytes.length + ")");
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
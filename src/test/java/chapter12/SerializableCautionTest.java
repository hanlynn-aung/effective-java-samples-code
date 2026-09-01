package chapter12;

import chapter12.bad.BadPrecariousSerializable;
import chapter12.good.GoodMinimalSerializable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SerializableCautionTest {

    @Test
    @DisplayName("Bad: a mutable, extendable Serializable class serializes its whole representation")
    void badSerializesInternals() throws Exception {
        BadPrecariousSerializable v = new BadPrecariousSerializable("next");
        // Serialize via default form: writes the public mutable field directly.
        byte[] bytes;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(v);
            bytes = bos.toByteArray();
        }
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            BadPrecariousSerializable back = (BadPrecariousSerializable) in.readObject();
            assertEquals("next", back.title);
        }
    }

    @Test
    @DisplayName("Good: a deliberate, immutable Serializable with explicit serialVersionUID round-trips")
    void goodRoundTrips() throws Exception {
        GoodMinimalSerializable v = new GoodMinimalSerializable("next");
        byte[] bytes;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(v);
            bytes = bos.toByteArray();
        }
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            GoodMinimalSerializable back = (GoodMinimalSerializable) in.readObject();
            assertEquals("next", back.title());
        }
    }
}
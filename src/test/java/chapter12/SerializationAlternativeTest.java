package chapter12;

import chapter12.bad.BadJavaSerialization;
import chapter12.good.GoodDataFormat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SerializationAlternativeTest {

    @Test
    @DisplayName("Bad: raw Java serialization round-trips but is an opaque binary blob")
    void badJavaSerializationRoundTrips() throws Exception {
        BadJavaSerialization v = new BadJavaSerialization("Han", 30);
        byte[] bytes = v.toBytes();
        // Binary gadget-interchange; opaque and tied to class internal layout.
        BadJavaSerialization back = BadJavaSerialization.fromBytes(bytes);
        assertEquals("Han", back.name());
        assertEquals(30, back.age());
    }

    @Test
    @DisplayName("Good: a stable, versioned text format round-trips exactly")
    void goodTextFormatRoundTrips() {
        GoodDataFormat v = new GoodDataFormat("Han", 30);
        String record = v.toRecord();
        GoodDataFormat back = GoodDataFormat.parse(record);
        assertEquals("Han", back.name());
        assertEquals(30, back.age());
    }

    @Test
    @DisplayName("Good: the text format rejects unknown versions / shapes")
    void goodTextFormatRejectsBadRecords() {
        assertThrows(IllegalArgumentException.class, () -> GoodDataFormat.parse("v9|GoodDataFormat|Han|30"));
        assertThrows(IllegalArgumentException.class, () -> GoodDataFormat.parse("nonsense"));
    }
}
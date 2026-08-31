package chapter2.item3;

import chapter2.item3.bad.BadSerializableSingleton;
import chapter2.item3.bad.BadSingleton;
import chapter2.item3.good.GoodSingleton;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SingletonTest {

    @Test
    @DisplayName("Bad: reflection can create a second field-based singleton")
    void reflectionCreatesSecondInstance() throws Exception {
        Constructor<BadSingleton> ctor = BadSingleton.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        assertNotSame(BadSingleton.getInstance(), ctor.newInstance());
    }

    @Test
    @DisplayName("Good: enum rejects reflective instantiation (JVM-enforced)")
    void enumRejectsReflection() throws Exception {
        Constructor<?> ctor = GoodSingleton.class.getDeclaredConstructors()[0];
        ctor.setAccessible(true);
        assertThrows(IllegalArgumentException.class,
                () -> ctor.newInstance("INSTANCE", 0));
    }

    @Test
    @DisplayName("Bad: deserialization produces a NEW field-based singleton")
    void fieldSingletonBreaksOnDeserialization() throws Exception {
        BadSerializableSingleton copy =
                (BadSerializableSingleton) roundTrip(BadSerializableSingleton.getInstance());
        assertNotSame(BadSerializableSingleton.getInstance(), copy);
    }

    @Test
    @DisplayName("Good: enum singleton survives serialization round-trip")
    void enumSingletonSurvivesSerialization() throws Exception {
        GoodSingleton copy = (GoodSingleton) roundTrip(GoodSingleton.INSTANCE);
        assertSame(GoodSingleton.INSTANCE, copy);
    }

    private static Object roundTrip(Object value) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
            out.writeObject(value);
        }
        try (ObjectInputStream in = new ObjectInputStream(
                new ByteArrayInputStream(bytes.toByteArray()))) {
            return in.readObject();
        }
    }
}
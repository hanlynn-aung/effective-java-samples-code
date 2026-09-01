package chapter12.bad;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.io.Serializable;

/**
 * (bad) Uses raw Java serialization as the interchange format. The blob is a
 * private binary format tied to the class's internal layout: renaming a field,
 * changing its type, or restructuring the class silently corrupts or breaks
 * reading older bytes, and every deserialization is a gadget-injection risk.
 */
public final class BadJavaSerialization implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String name;
    private final int age;

    public BadJavaSerialization(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public byte[] toBytes() throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(this);
            return bos.toByteArray();
        }
    }

    public static BadJavaSerialization fromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return (BadJavaSerialization) in.readObject();
        }
    }

    public String name() {
        return name;
    }

    public int age() {
        return age;
    }
}
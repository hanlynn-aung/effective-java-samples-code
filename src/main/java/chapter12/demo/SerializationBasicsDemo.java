package chapter12.demo;

import chapter12.bad.BadDefaultSerializedForm;
import chapter12.bad.BadJavaSerialization;
import chapter12.good.GoodCustomSerializedForm;
import chapter12.good.GoodDataFormat;
import chapter12.good.GoodMinimalSerializable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

/**
 * Demonstrates items 85-87: preferring alternatives to Java serialization,
 * caution with Serializable, and custom serialized forms.
 */
public final class SerializationBasicsDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Item 85: prefer alternatives to Java serialization ===");
        BadJavaSerialization bin = new BadJavaSerialization("Han", 30);
        byte[] raw = bin.toBytes();
        System.out.println("    bad (raw ObjectOutputStream) -> " + raw.length + " opaque bytes");
        System.out.println("    bad round-trip: " + BadJavaSerialization.fromBytes(raw).name()
                + " (binary, tied to internal class layout)");

        GoodDataFormat txt = new GoodDataFormat("Han", 30);
        String record = txt.toRecord();
        System.out.println("    good (stable text format): \"" + record + "\"");
        System.out.println("    good round-trip: " + GoodDataFormat.parse(record).name()
                + " (versioned, explicit, no gadget risk)");

        System.out.println();
        System.out.println("=== Item 86: implement Serializable with great caution ===");
        GoodMinimalSerializable safe = new GoodMinimalSerializable("next");
        System.out.println("    good: a deliberate, immutable Serializable -> " + safe
                + " (explicit serialVersionUID, stable form)");

        System.out.println();
        System.out.println("=== Item 87: consider a custom serialized form ===");
        Date start = new Date(1_000L);
        Date end = new Date(9_000L);

        byte[] badForm;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(new BadDefaultSerializedForm(start, end));
            badForm = bos.toByteArray();
        }
        byte[] goodForm;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(new GoodCustomSerializedForm(start, end));
            goodForm = bos.toByteArray();
        }
        System.out.println("    bad  default form: " + badForm.length + " bytes (serializes internal Date objects)");
        System.out.println("    good custom form: " + goodForm.length + " bytes (two longs - decoupled, minimal)");
    }
}
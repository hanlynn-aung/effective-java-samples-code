package chapter12.demo;

import chapter12.bad.BadDirectSerialization;
import chapter12.bad.BadUnvalidatedReadObject;
import chapter12.good.GoodDefensiveReadObject;
import chapter12.good.GoodEnumSingleton;
import chapter12.good.GoodSerializationProxy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

/**
 * Demonstrates items 88-90: defensive readObject, enum vs readResolve instance
 * control, and serialization proxies.
 */
public final class SerializationSafetyDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Item 88: write readObject methods defensively ===");
        GoodDefensiveReadObject good = new GoodDefensiveReadObject(new Date(1_000L), new Date(9_000L));
        long before = good.durationMillis();
        good.start().setTime(99_000L); // attacker mutates what the getter returns
        System.out.println("    good: getter returns a copy; corruption attempt ignored -> "
                + (good.durationMillis() == before ? "still " + before + "ms" : "CORRUPTED"));

        BadUnvalidatedReadObject bad = new BadUnvalidatedReadObject(new Date(1_000L), new Date(9_000L));
        long badBefore = bad.durationMillis();
        bad.start().setTime(99_000L);
        System.out.println("    bad: getter leaks internal mutable Date -> "
                + (bad.durationMillis() != badBefore ? "CORRUPTED to " + bad.durationMillis() + "ms" : "ok"));

        System.out.println();
        System.out.println("=== Item 89: prefer enums to readResolve for instance control ===");
        GoodEnumSingleton s1 = GoodEnumSingleton.INSTANCE;
        GoodEnumSingleton s2 = (GoodEnumSingleton) deserialize(serialize(s1));
        System.out.println("    enum singleton: deserialized is same instance? " + (s1 == s2));

        System.out.println();
        System.out.println("=== Item 90: prefer serialization proxies ===");
        GoodSerializationProxy proxy = new GoodSerializationProxy(new Date(1_000L), new Date(9_000L));
        GoodSerializationProxy proxyBack = (GoodSerializationProxy) deserialize(serialize(proxy));
        System.out.println("    proxy period duration after round-trip: "
                + proxyBack.durationMillis() + "ms (rebuilt via validating constructor)");

        System.out.println("    (bad direct serialization round-trips but no validation)");
        BadDirectSerialization rawPeriod = new BadDirectSerialization(new Date(1_000L), new Date(9_000L));
        Object rawBack = deserialize(serialize(rawPeriod));
        System.out.println("    bad direct period type after round-trip: "
                + rawBack.getClass().getSimpleName());
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
package chapter9.demo;

import chapter9.bad.BadBoxedTrap;
import chapter9.bad.BadConcatLoop;
import chapter9.bad.BadConcreteType;
import chapter9.bad.BadStringState;
import chapter9.good.GoodInterfaceType;
import chapter9.good.GoodPrimitives;
import chapter9.good.GoodStringBuilder;
import chapter9.good.GoodTypedEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PrimitivesAndTypesDemo {

    public static void main(String[] args) {
        item61Primitives();
        item62Strings();
        item63Concat();
        item64Interfaces();
    }

    static void item61Primitives() {
        System.out.println("== Item 61: prefer primitives to boxed primitives ==");
        BadBoxedTrap bad = new BadBoxedTrap();
        System.out.println("bad: == on boxed Integers (1000,1000) is "
                + bad.sameRank(1000, 1000) + " (identity, not value!)");
        Map<String, Integer> map = new HashMap<>();
        map.put("b", null);
        try {
            new BadBoxedTrap().maxUnboxed(map);
        } catch (NullPointerException e) {
            System.out.println("bad: unboxing a null Integer -> " + e);
        }
        GoodPrimitives good = new GoodPrimitives();
        System.out.println("good: == on primitive longs (1000,1000) is "
                + good.sameRank(1000, 1000) + " (value semantics)");
    }

    static void item62Strings() {
        System.out.println();
        System.out.println("== Item 62: avoid strings where other types fit ==");
        System.out.println("bad: new BadStringState(\"Ready\").isReady() = "
                + new BadStringState("Ready").isReady() + " (magic-string casing breaks it)");
        GoodTypedEnum good = new GoodTypedEnum(GoodTypedEnum.Status.READY);
        System.out.println("good: enum Status.READY.isReady() = " + good.isReady()
                + " (type-safe, no spelling/case errors)");
    }

    static void item63Concat() {
        System.out.println();
        System.out.println("== Item 63: beware string concatenation ==");
        String fragment = "x";
        int times = 50_000;
        long tBad = time(() -> new BadConcatLoop().repeat(fragment, times));
        long tGood = time(() -> new GoodStringBuilder().repeat(fragment, times));
        System.out.println("+= in a loop  (" + times + " iters): " + tBad + " ms (O(n^2))");
        System.out.println("StringBuilder (" + times + " iters): " + tGood + " ms (O(n))");
    }

    static void item64Interfaces() {
        System.out.println();
        System.out.println("== Item 64: refer to objects by their interfaces ==");
        BadConcreteType bad = new BadConcreteType(
                new ArrayList<>(List.of("x", "y")), new HashMap<>());
        System.out.println("bad: locked to ArrayList/HashMap, callers can't swap impls");
        GoodInterfaceType good = new GoodInterfaceType(
                new ArrayList<>(List.of("x", "y")), new HashMap<>());
        System.out.println("good: declared as List/Map, any impl works ("
                + good.names().size() + " names)");
    }

    interface Timed {
        void run();
    }

    private static long time(Timed timed) {
        long start = System.nanoTime();
        timed.run();
        return (System.nanoTime() - start) / 1_000_000;
    }
}
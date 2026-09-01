package chapter9.demo;

import chapter9.bad.BadAlwaysNative;
import chapter9.bad.BadNaming;
import chapter9.bad.BadPreoptimize;
import chapter9.bad.BadReflective;
import chapter9.good.GoodInterfaceInvocation;
import chapter9.good.GoodJavaFirst;
import chapter9.good.GoodMeasureFirst;
import chapter9.good.GoodNaming;

import java.util.concurrent.ThreadLocalRandom;

public final class InterfacesAndOptimizationDemo {

    public static void main(String[] args) {
        item65Reflection();
        item66Native();
        item67Optimize();
        item68Naming();
    }

    static void item65Reflection() {
        System.out.println("== Item 65: prefer interfaces to reflection ==");
        GoodInterfaceInvocation good = new GoodInterfaceInvocation(
                new GoodInterfaceInvocation.FriendlyGreeter());
        System.out.println("good (interface): " + good.buildGreeting("Ada"));
        try {
            new BadReflective().buildGreeting("chapter9.good.FriendlyGreter", "Ada");
        } catch (IllegalArgumentException e) {
            System.out.println("bad (reflection): ClassNotFoundException hidden in "
                    + e.getClass().getSimpleName() + " (typo found only at runtime)");
        }
    }

    static void item66Native() {
        System.out.println();
        System.out.println("== Item 66: use native methods judiciously ==");
        GoodJavaFirst good = new GoodJavaFirst();
        System.out.println("good (pure Java): uppercase=\"" + good.uppercaseJava("ada")
                + "\", time=" + good.currentTimeJava());
        System.out.println("bad (JNI-first): currentTimeNative()/uppercaseNative() rejected - "
                + "the JDK already does both");
    }

    static void item67Optimize() {
        System.out.println();
        System.out.println("== Item 67: optimize judiciously - measure first ==");
        long[] values = new long[1_000_000];
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        for (int i = 0; i < values.length; i++) {
            values[i] = rng.nextInt(0, 1000);
        }
        long bad = new BadPreoptimize().sumFirst(values);
        long good = new GoodMeasureFirst().sumFirst(values);
        System.out.println("bad (bit-twiddling) and good (simple for-each) both = " + bad
                + " - prefer the readable one until profiling proves a hot spot");
    }

    static void item68Naming() {
        System.out.println();
        System.out.println("== Item 68: adhere to naming conventions ==");
        BadNaming bad = new BadNaming(100);
        bad.st(120);
        System.out.println("bad: n/x/gv()/st()/chk() - what does chk() return? "
                + bad.chk());
        GoodNaming good = new GoodNaming(100);
        good.setValue(120);
        System.out.println("good: isAtOrAboveLimit() = " + good.isAtOrAboveLimit()
                + " (self-evident)");
    }
}
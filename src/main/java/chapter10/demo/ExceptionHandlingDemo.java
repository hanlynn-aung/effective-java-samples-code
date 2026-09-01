package chapter10.demo;

import chapter10.bad.BadNonAtomic;
import chapter10.bad.BadSwallowed;
import chapter10.bad.BadUndocumented;
import chapter10.bad.BadVagueMessage;
import chapter10.good.GoodAtomic;
import chapter10.good.GoodDetailMessage;
import chapter10.good.GoodDocExceptions;
import chapter10.good.GoodHandle;

import java.util.Arrays;

public final class ExceptionHandlingDemo {

    public static void main(String[] args) {
        item74DocumentExceptions();
        item75DetailMessages();
        item76FailureAtomicity();
        item77DoNotIgnore();
    }

    static void item74DocumentExceptions() {
        System.out.println("== Item 74: document all exceptions thrown ==");
        System.out.println("bad: BadUndocumented.twoDigitYear - no @throws, negative year is silent");
        try {
            new GoodDocExceptions().twoDigitYear(-1);
        } catch (IllegalArgumentException e) {
            System.out.println("good: documented @throws honoured -> " + e.getMessage());
        }
    }

    static void item75DetailMessages() {
        System.out.println();
        System.out.println("== Item 75: include failure-capture info in detail messages ==");
        try {
            new BadVagueMessage().at("abc", 9);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("bad: '" + e.getMessage() + "' - can't reproduce");
        }
        try {
            new GoodDetailMessage().at("abc", 9);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("good: '" + e.getMessage() + "' says exactly what went wrong");
        }
    }

    static void item76FailureAtomicity() {
        System.out.println();
        System.out.println("== Item 76: strive for failure atomicity ==");
        BadNonAtomic bad = new BadNonAtomic();
        try {
            bad.addBatch(Arrays.asList("a", "b", null));
        } catch (IllegalArgumentException e) {
            System.out.println("bad: failed batch left " + bad.items().size()
                    + " items committed (partial write!)");
        }
        GoodAtomic good = new GoodAtomic();
        try {
            good.addBatch(Arrays.asList("a", "b", null));
        } catch (IllegalArgumentException e) {
            System.out.println("good: failed batch left " + good.items().size()
                    + " items - object unchanged (atomic)");
        }
    }

    static void item77DoNotIgnore() {
        System.out.println();
        System.out.println("== Item 77: don't ignore exceptions ==");
        BadSwallowed bad = new BadSwallowed();
        System.out.println("bad: safeParse(\"x\") silently returns " + bad.safeParse("x")
                + " - caller can't distinguish error from a real -1");
        try {
            new GoodHandle().safeParse("x");
        } catch (IllegalArgumentException e) {
            System.out.println("good: surfaced with context -> " + e.getMessage());
        }
    }
}
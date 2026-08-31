package chapter6.demo;

import chapter6.bad.BadFrozenOperation;
import chapter6.bad.BadMeritLevels;
import chapter6.bad.BadOrdinalGardener;
import chapter6.bad.BadStatus;
import chapter6.bad.BadTextStyle;
import chapter6.good.GoodBasicOperation;
import chapter6.good.GoodEnumMapGardener;
import chapter6.good.GoodExtendedOperation;
import chapter6.good.GoodMeritLevels;
import chapter6.good.GoodOperation;
import chapter6.good.GoodStatus;
import chapter6.good.GoodStyle;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EnumDemo {

    public static void main(String[] args) {
        item34Enums();
        item35InstanceFields();
        item36EnumSet();
        item37EnumMap();
        item38Extensible();
    }

    private static void item34Enums() {
        System.out.println("== Item 34: enums over int constants ==");

        BadStatus bad = new BadStatus();
        System.out.println("bad int constants: describe(FAILED)=" + bad.describe(2)
                + ", describe(999)=" + bad.describe(999) + " (garbage silently accepted)");

        System.out.println("good enum: type-safe, any invalid value fails to compile; values=");
        for (GoodStatus status : GoodStatus.values()) {
            System.out.println("  " + status + " -> " + status.description());
        }
    }

    private static void item35InstanceFields() {
        System.out.println();
        System.out.println("== Item 35: instance fields over ordinals ==");

        System.out.println("bad ordinal ranks (LOW,HIGH,MEDIUM declared in that order):");
        for (BadMeritLevels.BadMerit m : BadMeritLevels.BadMerit.values()) {
            System.out.println("  " + m + " rank=" + m.rank());
        }
        System.out.println("  -> MEDIUM reports rank 3 though it is the 2nd tier. Silent corruption.");

        System.out.println("good explicit tiers (order-independent):");
        for (GoodMeritLevels.GoodMerit m : GoodMeritLevels.GoodMerit.values()) {
            System.out.println("  " + m + " tier=" + m.tier());
        }
    }

    private static void item36EnumSet() {
        System.out.println();
        System.out.println("== Item 36: EnumSet over bit fields ==");

        BadTextStyle bad = new BadTextStyle();
        int mask = BadTextStyle.STYLE_BOLD | BadTextStyle.STYLE_ITALIC | 128;
        System.out.println("bad bit field: apply(bold|italic|128)=" + bad.apply(mask)
                + " (128 is not a defined style, silently ignored)");

        GoodStyle good = new GoodStyle();
        Set<GoodStyle.Style> styles = EnumSet.of(GoodStyle.Style.BOLD, GoodStyle.Style.ITALIC);
        System.out.println("good EnumSet: apply(" + styles + ")=" + good.apply(styles));
    }

    private static void item37EnumMap() {
        System.out.println();
        System.out.println("== Item 37: EnumMap over ordinal indexing ==");

        List<BadOrdinalGardener.Plant> badPlants = List.of(
                new BadOrdinalGardener.Plant("rose", BadOrdinalGardener.LifeCycle.PERENNIAL));
        List<BadOrdinalGardener.Plant>[] badBuckets = BadOrdinalGardener.classify(badPlants);
        System.out.println("bad ordinal buckets: annual bucket = "
                + badBuckets[BadOrdinalGardener.LifeCycle.ANNUAL.ordinal()]
                + " (a hole the API does not promise)");

        List<GoodEnumMapGardener.Plant> goodPlants = List.of(
                new GoodEnumMapGardener.Plant("rose", GoodEnumMapGardener.LifeCycle.PERENNIAL));
        Map<GoodEnumMapGardener.LifeCycle, List<GoodEnumMapGardener.Plant>> groups =
                GoodEnumMapGardener.classify(goodPlants);
        System.out.println("good EnumMap: every lifecycle keyed -> " + groups);
    }

    private static void item38Extensible() {
        System.out.println();
        System.out.println("== Item 38: emulate extensible enums with interfaces ==");

        System.out.println("bad frozen enum: only " + java.util.Arrays.toString(BadFrozenOperation.values())
                + " - adding EXP forces editing shared code");

        System.out.println("good extensible enums via GoodOperation interface:");
        compute(GoodBasicOperation.PLUS, 2, 3);
        compute(GoodExtendedOperation.EXP, 2, 5);
        compute(GoodExtendedOperation.REMAINDER, 7, 5);

        GoodOperation op = GoodExtendedOperation.EXP;
        System.out.println("  one interface-typed reference dispatches to " + op + " -> "
                + op.apply(2, 5));
    }

    private static void compute(GoodOperation op, double x, double y) {
        System.out.println("  " + op.symbol() + " " + x + "," + y + " = " + op.apply(x, y));
    }
}
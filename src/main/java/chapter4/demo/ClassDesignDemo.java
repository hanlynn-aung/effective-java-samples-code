package chapter4.demo;

import chapter4.bad.BadExplodingCounter;
import chapter4.bad.BadExposedLedger;
import chapter4.bad.BadInstrumentedHashSet;
import chapter4.bad.BadMutableTime;
import chapter4.bad.BadNewspaper;
import chapter4.bad.FragileBaseCounter;
import chapter4.good.GoodCapsuleLedger;
import chapter4.good.GoodFinalCounter;
import chapter4.good.GoodInstrumentedSet;
import chapter4.good.GoodNewspaper;
import chapter4.good.GoodStack;
import chapter4.good.GoodTime;

import java.util.List;

public class ClassDesignDemo {

    public static void main(String[] args) {
        item15Accessibility();
        item16Accessors();
        item17Mutability();
        item18Composition();
        item19Inheritance();
    }

    private static void item15Accessibility() {
        System.out.println("== Item 15: minimize accessibility ==");

        BadExposedLedger badly = new BadExposedLedger();
        badly.record("alice", 100L);
        badly.entries.clear(); // anyone can tamper
        System.out.println("bad exposed ledger: map cleared behind the class's back -> balance="
                + badly.entries.get("alice"));

        GoodCapsuleLedger well = GoodCapsuleLedger.create();
        well.record("alice", 100L);
        well.record("alice", 50L);
        System.out.println("good capsule ledger: balance=" + well.balanceOf("alice")
                + " total=" + well.total() + " (no public mutable state)");
    }

    private static void item16Accessors() {
        System.out.println();
        System.out.println("== Item 16: accessor methods over public fields ==");

        BadNewspaper naked = new BadNewspaper();
        naked.headline = "Real headline";
        naked.headline = "Tampered!";
        System.out.println("bad naked fields: headline silently rewritten to " + naked.headline);

        GoodNewspaper guarded = new GoodNewspaper("Peace", "Hanlynn Aung", List.of("a", "b"));
        List<String> view = guarded.articles();
        view.add("intruder");
        System.out.println("good accessors: headline=" + guarded.headline()
                + " articles=" + guarded.articles() + " (returned list is a copy)");
    }

    private static void item17Mutability() {
        System.out.println();
        System.out.println("== Item 17: minimize mutability ==");

        BadMutableTime mutable = new BadMutableTime(10, 30);
        mutable.setMinute(45);
        System.out.println("bad mutable: object changed to " + mutable);

        GoodTime original = new GoodTime(10, 30);
        GoodTime shifted = original.withHour(22);
        System.out.println("good immutable: withHour(22) -> " + shifted
                + " while original stays " + original);
    }

    private static void item18Composition() {
        System.out.println();
        System.out.println("== Item 18: composition over inheritance ==");

        BadInstrumentedHashSet<String> badSet = new BadInstrumentedHashSet<>();
        badSet.addAll(List.of("a", "b", "c"));
        System.out.println("bad HashSet subclass: addAll(3 elements) -> addCount="
                + badSet.getAddCount() + " (double-counted, should be 3)");

        GoodInstrumentedSet<String> goodSet = GoodInstrumentedSet.of();
        goodSet.addAll(List.of("a", "b", "c"));
        goodSet.add("a");
        goodSet.add("d");
        System.out.println("good composite set: size=" + goodSet.size() + " addCount="
                + goodSet.getAddCount() + " (duplicates counted only once)");

        GoodStack stack = new GoodStack();
        stack.push("a");
        stack.push("b");
        System.out.println("good stack: pop=" + stack.pop() + " -> nothing but push/pop is exposed");
    }

    private static void item19Inheritance() {
        System.out.println();
        System.out.println("== Item 19: design for inheritance or prohibit it ==");

        try {
            new BadExplodingCounter();
        } catch (NullPointerException e) {
            System.out.println("bad subclass: construction throws "
                    + e.getClass().getSimpleName()
                    + " (super constructor dispatched into an override using not-yet-initialized fields)");
        }

        GoodFinalCounter counter = new GoodFinalCounter();
        counter.add(2);
        counter.add(3);
        System.out.println("good final counter: add(2)+add(3) -> count=" + counter.count() + " (no hooks)");
    }
}
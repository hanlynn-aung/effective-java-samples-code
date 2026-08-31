package chapter9.demo;

import chapter9.bad.BadDollarsDouble;
import chapter9.bad.BadIndexLoop;
import chapter9.bad.BadReinvent;
import chapter9.bad.BadWideScope;
import chapter9.good.GoodDollarsBigDecimal;
import chapter9.good.GoodForEach;
import chapter9.good.GoodNarrowScope;
import chapter9.good.GoodUseLibraries;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class GeneralProgrammingDemo {

    public static void main(String[] args) {
        item57Scope();
        item58Loops();
        item59Libraries();
        item60Money();
    }

    static void item57Scope() {
        System.out.println("== Item 57: minimize the scope of local variables ==");
        List<Integer> values = Arrays.asList(1, 2, 3);
        System.out.println("bad (index + field leak): sum=" + new BadWideScope().sum(values)
                + " but `i`/`total` stay reachable past the loop");
        System.out.println("good (declare-in-loop):   sum="
                + new GoodNarrowScope().sum(values) + " no stale state survives");
    }

    static void item58Loops() {
        System.out.println();
        System.out.println("== Item 58: prefer for-each loops ==");
        List<String> list = Arrays.asList("", "a", "", "bb");
        System.out.println("bad indexed for:  empty=" + new BadIndexLoop(list).countEmpty()
                + " (requires List.get(i), breaks on a Set)");
        Set<String> set = new HashSet<>(Arrays.asList("", "a", ""));
        System.out.println("good for-each:    list empty="
                + new GoodForEach(list).countEmpty() + ", set empty="
                + new GoodForEach(set).countEmpty() + " (works on any Iterable)");
    }

    static void item59Libraries() {
        System.out.println();
        System.out.println("== Item 59: know and use the libraries ==");
        BadReinvent bad = new BadReinvent();
        GoodUseLibraries good = new GoodUseLibraries();
        System.out.println("bad: Math.random()*bound=" + bad.randomWithRange(5)
                + " (rolling my own), join=" + bad.join(Arrays.asList("a","b","c")));
        System.out.println("good: ThreadLocalRandom=" + good.randomWithRange(5)
                + ", String.join=" + good.join(Arrays.asList("a","b","c"))
                + ", Math.max=" + good.max(1.0, 2.0));
    }

    static void item60Money() {
        System.out.println();
        System.out.println("== Item 60: avoid float/double for exact answers ==");
        BadDollarsDouble bad = new BadDollarsDouble(0.10);
        bad.add(0.20);
        bad.add(0.30);
        System.out.println("bad double: 0.10+0.20+0.30 = " + bad.balance()
                + "  (should be 0.60, drifts in binary)");
        GoodDollarsBigDecimal good = new GoodDollarsBigDecimal("0.10");
        good.add("0.20");
        good.add("0.30");
        System.out.println("good BigDecimal: " + good.balance() + " (exact to the cent)");
    }
}
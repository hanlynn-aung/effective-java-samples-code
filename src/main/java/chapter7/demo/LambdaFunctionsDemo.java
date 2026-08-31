package chapter7.demo;

import chapter7.bad.BadFormatter;
import chapter7.bad.BadInventedEvent;
import chapter7.bad.BadVerbose;
import chapter7.good.GoodConcise;
import chapter7.good.GoodFormatter;
import chapter7.good.GoodUseStandard;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class LambdaFunctionsDemo {

    public static void main(String[] args) {
        item42Lambdas();
        item43MethodReferences();
        item44StandardInterfaces();
    }

    private static void item42Lambdas() {
        System.out.println("== Item 42: lambdas over anonymous classes ==");

        String text = new BadFormatter().format("  HeLLo  ");
        String same = new GoodFormatter().format("  HeLLo  ");
        System.out.println("bad anonymous: " + text);
        System.out.println("good lambda:   " + same + "  (identical, but lambda is 1 line vs 10)");
    }

    private static void item43MethodReferences() {
        System.out.println();
        System.out.println("== Item 43: method references over lambdas ==");

        String[] words = {"a", "bcd", "very-long", "hi"};
        GoodConcise good = new GoodConcise();
        System.out.println("good method refs: countShorts=" + good.countShorts(words)
                + ", toList=" + good.toList(words));

        BadVerbose bad = new BadVerbose();
        System.out.println("bad verbose lambdas: countShorts=" + bad.countShorts(words)
                + "  (same result, harder to read)");

        Function<String, Integer> len = good.lengthOf();
        System.out.println("String::length ref: lengthOf('hello')=" + len.apply("hello"));
    }

    private static void item44StandardInterfaces() {
        System.out.println();
        System.out.println("== Item 44: prefer standard functional interfaces ==");

        BadInventedEvent event = new BadInventedEvent();
        event.setListener(price -> System.out.println("  bad invented PriceListener fired: " + price));
        event.onPrice(9.99);

        GoodUseStandard good = new GoodUseStandard();
        List<String> seen = new ArrayList<>();
        BiConsumer<String, Double> reporter = (t, p) -> seen.add(t + "=" + p);
        good.onPrice(reporter, "AAPL", 9.99);
        System.out.println("  good standard BiConsumer: " + seen + " (no custom interface needed)");
    }
}
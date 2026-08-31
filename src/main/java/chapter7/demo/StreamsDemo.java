package chapter7.demo;

import chapter7.bad.BadParallelSum;
import chapter7.bad.BadStatefulCollect;
import chapter7.bad.BadStreamOnlyApi;
import chapter7.bad.BadStreamSpaghetti;
import chapter7.good.GoodClearStreams;
import chapter7.good.GoodCollectionApi;
import chapter7.good.GoodCollectors;
import chapter7.good.GoodParallelReduce;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class StreamsDemo {

    private static final String[] LINES = {"Hello World", "Java 17 Streams", "One Two"};

    public static void main(String[] args) {
        item45Judiciously();
        item46SideEffectFree();
        item47CollectionReturn();
        item48ParallelCaution();
    }

    private static void item45Judiciously() {
        System.out.println("== Item 45: use streams judiciously ==");

        Map<String, Long> spaghetti =
                new BadStreamSpaghetti().letterCountsAcrossLines(LINES);
        Map<Character, Long> clear = new GoodClearStreams().letterCounts(LINES);
        System.out.println("bad spaghetti chain: " + spaghetti);
        System.out.println("good clear loop:     " + clear + "  (same answer, far easier to read)");
    }

    private static void item46SideEffectFree() {
        System.out.println();
        System.out.println("== Item 46: prefer side-effect-free functions ==");

        String[] words = {"one", "two", "three", "four"};
        List<String> bad = new BadStatefulCollect().uppercaseTopWords(words, 3);
        List<String> good = new GoodCollectors().uppercaseTopWords(words, 3);
        System.out.println("bad forEach into external list: " + bad
                + "  (works only because the stream stayed sequential)");
        System.out.println("good Collectors.toList:         " + good + "  (pure, order-independent)");
    }

    private static void item47CollectionReturn() {
        System.out.println();
        System.out.println("== Item 47: prefer Collection to Stream as a return type ==");

        BadStreamOnlyApi bad = new BadStreamOnlyApi();
        var badStream = bad.recentScoresStream();
        long first = badStream.count();
        try {
            badStream.count();
        } catch (IllegalStateException e) {
            System.out.println("bad Stream API: first read count=" + first
                    + ", then re-read -> " + e);
        }

        GoodCollectionApi good = new GoodCollectionApi();
        var scores = good.recentScores();
        System.out.println("good Collection API: size=" + scores.size() + " re-iterable="
                + scores.stream().map(t -> t.key).toList());
    }

    private static void item48ParallelCaution() {
        System.out.println();
        System.out.println("== Item 48: caution with parallel streams ==");

        int[] values = IntStream.range(0, 1_000_000).toArray();
        long expected = 1_000_000L * 999_999L / 2L;
        long racy = new BadParallelSum().badParallelSum(values);
        long good = new GoodParallelReduce().sum(values);
        System.out.println("bad parallel forEach into shared accumulator: " + racy
                + "  (expected " + expected + ", racy on multicore)");
        System.out.println("good parallel reduce (associative):           " + good
                + "  (exact)");
    }
}
package chapter7.good;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class GoodUseStandard {

    public void onPrice(BiConsumer<String, Double> reporter, String ticker, double price) {
        reporter.accept(ticker, price);
    }

    public Predicate<String> shorterThan(int limit) {
        return s -> s.length() < limit;
    }

    public Function<Integer, String> asRoman() {
        return this::toRoman;
    }

    public Supplier<List<String>> newLogProvider() {
        return ArrayList::new;
    }

    private String toRoman(int n) {
        if (n == 1) {
            return "I";
        }
        return String.valueOf(n);
    }
}
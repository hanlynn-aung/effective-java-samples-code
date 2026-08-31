package chapter7.good;

import java.util.function.Function;

public final class GoodFormatter {
    private static final Function<String, String> NORMALIZE =
            value -> value.trim().toUpperCase();

    public String format(String value) {
        return NORMALIZE.apply(value);
    }
}

package chapter8.bad;

import java.util.List;
import java.util.Optional;

public final class BadOptionalOveruse {

    public Optional<List<String>> words() {
        return Optional.of(List.of("a", "b"));
    }

    public Optional<Double> lastPrice() {
        return Optional.of(12.50);
    }
}
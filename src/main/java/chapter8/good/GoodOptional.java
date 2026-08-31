package chapter8.good;

import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

public final class GoodOptional {

    public List<String> words() {
        return List.of("a", "b");
    }

    public OptionalDouble lastPrice() {
        return OptionalDouble.of(12.50);
    }

    public Optional<String> middle(String value) {
        if (value == null || value.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(value.substring(1, value.length() - 1));
    }
}
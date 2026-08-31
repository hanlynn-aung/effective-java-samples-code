package chapter9.good;

public final class GoodParser {
    public int parse(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("value must not be blank");
        }
        return Integer.parseInt(value);
    }
}

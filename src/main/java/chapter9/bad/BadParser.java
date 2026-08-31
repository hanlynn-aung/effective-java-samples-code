package chapter9.bad;

public final class BadParser {
    public int parse(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }
}

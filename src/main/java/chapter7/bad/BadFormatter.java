package chapter7.bad;

public final class BadFormatter {
    public String format(String value) {
        return new java.util.function.Function<String, String>() {
            @Override public String apply(String input) {
                return input.trim().toUpperCase();
            }
        }.apply(value);
    }
}

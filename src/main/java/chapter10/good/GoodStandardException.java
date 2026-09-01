package chapter10.good;

public final class GoodStandardException {

    public int requiredIndex(String value) {
        if (value == null) {
            throw new NullPointerException("value");
        }
        if (value.isEmpty()) {
            throw new IllegalArgumentException("value must not be empty");
        }
        return value.length() - 1;
    }
}
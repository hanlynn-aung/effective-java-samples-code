package chapter2.item6.bad;

public final class BadRegexMatcher {
    public boolean isEmail(String value) {
        return value.matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}");
    }
}
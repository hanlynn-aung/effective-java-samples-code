package chapter2.item6.good;

import java.util.regex.Pattern;

public final class GoodRegexMatcher {
    private static final Pattern EMAIL = Pattern.compile(
            "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}");

    public boolean isEmail(String value) {
        return EMAIL.matcher(value).matches();
    }
}
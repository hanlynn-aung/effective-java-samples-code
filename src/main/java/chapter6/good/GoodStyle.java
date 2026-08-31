package chapter6.good;

import java.util.Set;

public final class GoodStyle {

    public enum Style {
        PLAIN, BOLD, ITALIC, UNDERLINE
    }

    public String apply(Set<Style> styles) {
        StringBuilder result = new StringBuilder("text");
        if (styles.contains(Style.BOLD)) {
            result.append("+bold");
        }
        if (styles.contains(Style.ITALIC)) {
            result.append("+italic");
        }
        if (styles.contains(Style.UNDERLINE)) {
            result.append("+underline");
        }
        return result.toString();
    }
}
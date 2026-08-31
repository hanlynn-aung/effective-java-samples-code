package chapter2.item6.bad;

import java.time.LocalDate;

public final class BadDateFormatter {
    public String format(LocalDate date) {
        return new StringBuilder().append(date).toString();
    }
}

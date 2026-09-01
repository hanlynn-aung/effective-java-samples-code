package chapter10.bad;

public final class BadUndocumented {

    /* no doc comment at all */
    public int twoDigitYear(int year) {
        if (year < 0) {
            throw new IllegalArgumentException("bad year");
        }
        return year % 100;
    }
}
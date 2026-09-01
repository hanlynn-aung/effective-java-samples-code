package chapter10.good;

/**
 * Helper for deriving two-digit years.
 */
public final class GoodDocExceptions {

    /**
     * Returns the last two digits of the century-wide year.
     *
     * @param year the full year, which must be non-negative
     * @return {@code year % 100}
     * @throws IllegalArgumentException if {@code year} is negative
     * @apiNote callers wanting a century-aware value should use
     *          {@link java.time.Year} instead
     */
    public int twoDigitYear(int year) {
        if (year < 0) {
            throw new IllegalArgumentException("year must be non-negative: " + year);
        }
        return year % 100;
    }
}
package chapter8.good;

/**
 * A mutable record of an item's sale price and its tax.
 *
 * <p>Instances are <em>not</em> thread-safe; callers must synchronize access.
 *
 * @author Learning Lab
 */
public final class GoodDocumented {

    /**
     * Applies the configured sales-tax rate to a pre-tax price.
     *
     * @param price the pre-tax price, which must be non-negative and not
     *              {@link Double#NaN NaN}
     * @return the tax owed, computed as {@code price * TAX_RATE}
     * @throws IllegalArgumentException if {@code price} is negative or NaN
     * @implSpec the result is not rounded; callers needing exact money should
     *           round explicitly
     */
    public double rate(double price) {
        if (Double.isNaN(price) || price < 0) {
            throw new IllegalArgumentException("price must be non-negative: " + price);
        }
        return price * TAX_RATE;
    }

    private static final double TAX_RATE = 0.08;
}
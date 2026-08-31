package chapter8.good;

public final class GoodCalculator {
    private static final double TAX_RATE = 0.20;

    public double total(double price) {
        return price + tax(price);
    }

    public double tax(double price) {
        return price * TAX_RATE;
    }
}

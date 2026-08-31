package chapter8.bad;

public final class BadCalculator {
    public double total(double price) {
        return price + price * 0.20;
    }

    public double tax(double price) {
        return price * 0.20;
    }
}

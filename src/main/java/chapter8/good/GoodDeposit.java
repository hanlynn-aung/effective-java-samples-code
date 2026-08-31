package chapter8.good;

public final class GoodDeposit {
    private double balance;

    public GoodDeposit(double initial) {
        requireNonNegative(initial);
        this.balance = initial;
    }

    public void deposit(double amount) {
        requirePositive(amount);
        balance += amount;
    }

    public double balance() {
        return balance;
    }

    private static void requireNonNegative(double value) {
        if (Double.isNaN(value) || value < 0) {
            throw new IllegalArgumentException("amount must be non-negative: " + value);
        }
    }

    private static void requirePositive(double value) {
        if (Double.isNaN(value) || value <= 0) {
            throw new IllegalArgumentException("amount must be positive: " + value);
        }
    }
}
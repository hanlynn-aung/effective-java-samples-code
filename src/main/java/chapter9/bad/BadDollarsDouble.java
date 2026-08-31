package chapter9.bad;

public final class BadDollarsDouble {

    private double balance;

    public BadDollarsDouble(double initial) {
        this.balance = initial;
    }

    public void add(double amount) {
        balance += amount;
    }

    public double balance() {
        return balance;
    }
}
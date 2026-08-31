package chapter8.bad;

public final class BadDeposit {
    private double balance;

    public void deposit(double amount) {
        balance += amount;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double balance() {
        return balance;
    }
}
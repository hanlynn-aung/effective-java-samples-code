package chapter10.good;

public final class GoodCheckedRuntime {

    public static final class InsufficientFundsException extends Exception {
        public InsufficientFundsException(String message) {
            super(message);
        }
    }

    public long withdraw(long balance, long amount) throws InsufficientFundsException {
        if (balance < amount) {
            throw new InsufficientFundsException(
                    "balance " + balance + " < amount " + amount);
        }
        return balance - amount;
    }

    public int divide(int a, int b) {
        if (b == 0) {
            throw new IllegalArgumentException("b must be non-zero");
        }
        return a / b;
    }

    public void setAge(int age) {
        if (age < 0) {
            throw new IllegalArgumentException("age must be non-negative: " + age);
        }
    }
}
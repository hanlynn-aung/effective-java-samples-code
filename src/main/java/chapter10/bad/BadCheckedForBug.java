package chapter10.bad;

public final class BadCheckedForBug {

    public static final class ProgrammingError extends Exception {
        public ProgrammingError(String message) {
            super(message);
        }
    }

    public int divide(int a, int b) throws ProgrammingError {
        if (b == 0) {
            throw new ProgrammingError("division by zero");
        }
        return a / b;
    }

    public void setAge(int age) throws ProgrammingError {
        if (age < 0 || age > 150) {
            throw new ProgrammingError("invalid age: " + age);
        }
    }
}
package chapter10.bad;

public final class BadInventedException {

    public static final class MyException extends Exception {
        public MyException(String message) {
            super(message);
        }
    }

    public int requiredIndex(String value) throws MyException {
        if (value == null) {
            throw new MyException("value was null");
        }
        if (value.isEmpty()) {
            throw new MyException("value was empty");
        }
        return value.length() - 1;
    }
}
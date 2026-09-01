package chapter10.good;

public final class GoodDetailMessage {

    public int divide(int a, int b) {
        if (b == 0) {
            throw new IllegalArgumentException("divide(" + a + ", 0) - divisor must not be zero");
        }
        return a / b;
    }

    public int at(String value, int index) {
        if (index >= value.length()) {
            throw new IndexOutOfBoundsException(
                    "index " + index + " out of bounds for length " + value.length());
        }
        return value.charAt(index);
    }
}
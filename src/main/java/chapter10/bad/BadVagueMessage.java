package chapter10.bad;

public final class BadVagueMessage {

    public int divide(int a, int b) {
        if (b == 0) {
            throw new IllegalArgumentException("invalid");
        }
        return a / b;
    }

    public int at(String value, int index) {
        if (index >= value.length()) {
            throw new IndexOutOfBoundsException("bad index");
        }
        return value.charAt(index);
    }
}
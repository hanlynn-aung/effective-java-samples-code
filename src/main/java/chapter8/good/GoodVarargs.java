package chapter8.good;

public final class GoodVarargs {

    public int sum(int... values) {
        int total = 0;
        for (int value : values) {
            total += value;
        }
        return total;
    }

    public int min(int first, int... rest) {
        int min = first;
        for (int value : rest) {
            if (value < min) {
                min = value;
            }
        }
        return min;
    }
}
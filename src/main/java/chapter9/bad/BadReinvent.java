package chapter9.bad;

import java.util.List;

public final class BadReinvent {

    public int randomWithRange(int bound) {
        return (int) (Math.random() * bound);
    }

    public String join(List<String> parts) {
        String result = "";
        for (String part : parts) {
            result += part;
        }
        return result;
    }

    public double max(double a, double b) {
        return a > b ? a : b;
    }
}
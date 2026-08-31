package chapter9.good;

import java.util.List;

public final class GoodNarrowScope {

    public int sum(List<Integer> values) {
        int total = 0;
        for (int value : values) {
            total += value;
        }
        return total;
    }

    public boolean containsPositive(List<Integer> values) {
        for (int value : values) {
            if (value > 0) {
                return true;
            }
        }
        return false;
    }
}